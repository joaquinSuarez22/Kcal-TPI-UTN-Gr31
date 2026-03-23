package com.example.tif_gr31.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tif_gr31.R;
import com.example.tif_gr31.api.ApiClient;
import com.example.tif_gr31.api.FoodProduct;
import com.example.tif_gr31.api.FoodSearchResponse;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegistrarComidaActivity extends AppCompatActivity {

    private static final String USDA_API_KEY = "BpuqGxfs81r5NeHjS2pe7fdWV2fRJY5vKd59lgnI";
    private static final String[] TIPOS_COMIDA = {"Desayuno", "Almuerzo", "Merienda", "Cena", "Snack"};

    private EditText etBuscarAlimento, etGramos;
    private ImageView btnLimpiarBusqueda;
    private RecyclerView rvResultadosBusqueda, rvIngredientesAgregados;
    private LinearLayout cardAlimentoSeleccionado;
    private TextView tvNombreAlimentoSeleccionado, tvTotalCalorias, tvTotalMacros;
    private TextView tvCaloriasCalculadas, tvProteinasCalculadas, tvCarbosCalculados, tvGrasasCalculadas;
    private Spinner spinnerTipoComida;
    private MaterialButton btnAgregarIngrediente, btnGuardarComida;

    private FirebaseFirestore db;
    private FirebaseAuth auth;

    private final List<Map<String, Object>> listaResultados = new ArrayList<>();
    private final List<Map<String, Object>> listaIngredientes = new ArrayList<>();
    private ResultadosAdapter resultadosAdapter;
    private IngredientesAdapter ingredientesAdapter;
    
    private FoodProduct alimentoActual;
    private double totalKcal = 0, totalProt = 0, totalCarb = 0, totalGrasa = 0;

    private final Handler debounceHandler = new Handler(Looper.getMainLooper());
    private Runnable debouncedSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar_comida);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        vincularVistas();
        configurarAdapters();
        configurarBusqueda();
        configurarSpinner();
        configurarCampoGramos();

        btnAgregarIngrediente.setOnClickListener(v -> agregarIngredienteALista());
        btnGuardarComida.setOnClickListener(v -> guardarComidaCompleta());
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
    }

    private void vincularVistas() {
        etBuscarAlimento = findViewById(R.id.etBuscarAlimento);
        btnLimpiarBusqueda = findViewById(R.id.btnLimpiarBusqueda);
        rvResultadosBusqueda = findViewById(R.id.rvResultadosBusqueda);
        rvIngredientesAgregados = findViewById(R.id.rvIngredientesAgregados);
        cardAlimentoSeleccionado = findViewById(R.id.cardAlimentoSeleccionado);
        tvNombreAlimentoSeleccionado = findViewById(R.id.tvNombreAlimentoSeleccionado);
        etGramos = findViewById(R.id.etGramos);
        btnAgregarIngrediente = findViewById(R.id.btnAgregarIngrediente);
        tvTotalCalorias = findViewById(R.id.tvTotalCalorias);
        tvTotalMacros = findViewById(R.id.tvTotalMacros);
        spinnerTipoComida = findViewById(R.id.spinnerTipoComida);
        btnGuardarComida = findViewById(R.id.btnGuardarComida);

        tvCaloriasCalculadas = findViewById(R.id.tvCaloriasCalculadas);
        tvProteinasCalculadas = findViewById(R.id.tvProteinasCalculadas);
        tvCarbosCalculados = findViewById(R.id.tvCarbosCalculados);
        tvGrasasCalculadas = findViewById(R.id.tvGrasasCalculadas);

        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.menu_inicio) {
                startActivity(new Intent(this, InicioActivity.class));
                return true;
            }
            return false;
        });
    }

    private void configurarAdapters() {
        rvResultadosBusqueda.setLayoutManager(new LinearLayoutManager(this));
        resultadosAdapter = new ResultadosAdapter(listaResultados, this::onAlimentoSeleccionadoDeBusqueda);
        rvResultadosBusqueda.setAdapter(resultadosAdapter);

        rvIngredientesAgregados.setLayoutManager(new LinearLayoutManager(this));
        ingredientesAdapter = new IngredientesAdapter(listaIngredientes);
        rvIngredientesAgregados.setAdapter(ingredientesAdapter);
    }

    private void configurarBusqueda() {
        etBuscarAlimento.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                String query = s.toString().trim();
                btnLimpiarBusqueda.setVisibility(query.isEmpty() ? View.GONE : View.VISIBLE);
                if (debouncedSearch != null) debounceHandler.removeCallbacks(debouncedSearch);
                if (query.length() < 2) {
                    rvResultadosBusqueda.setVisibility(View.GONE);
                    return;
                }
                debouncedSearch = () -> buscarEnUSDA(query);
                debounceHandler.postDelayed(debouncedSearch, 600);
            }
        });

        btnLimpiarBusqueda.setOnClickListener(v -> {
            etBuscarAlimento.setText("");
            rvResultadosBusqueda.setVisibility(View.GONE);
        });
    }

    private void configurarCampoGramos() {
        etGramos.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) {
                recalcularMacrosTemporales();
            }
        });
    }

    private void recalcularMacrosTemporales() {
        if (alimentoActual == null) return;
        String gramosStr = etGramos.getText().toString().trim();
        if (gramosStr.isEmpty()) {
            resetearDashboard();
            return;
        }

        try {
            double gramos = Double.parseDouble(gramosStr);
            double factor = gramos / 100.0;
            tvCaloriasCalculadas.setText(String.format(Locale.getDefault(), "%.0f", alimentoActual.getKcal() * factor));
            tvProteinasCalculadas.setText(String.format(Locale.getDefault(), "%.1f", alimentoActual.getProteinas() * factor));
            tvCarbosCalculados.setText(String.format(Locale.getDefault(), "%.1f", alimentoActual.getCarbohidratos() * factor));
            tvGrasasCalculadas.setText(String.format(Locale.getDefault(), "%.1f", alimentoActual.getGrasas() * factor));
        } catch (Exception e) {
            resetearDashboard();
        }
    }

    private void resetearDashboard() {
        tvCaloriasCalculadas.setText("0");
        tvProteinasCalculadas.setText("0");
        tvCarbosCalculados.setText("0");
        tvGrasasCalculadas.setText("0");
    }

    private void configurarSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, TIPOS_COMIDA);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTipoComida.setAdapter(adapter);
    }

    private void buscarEnUSDA(String query) {
        ApiClient.getService().buscarAlimentos(query, USDA_API_KEY, 15).enqueue(new Callback<FoodSearchResponse>() {
            @Override
            public void onResponse(Call<FoodSearchResponse> call, Response<FoodSearchResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<FoodProduct> foods = response.body().getFoods();
                    listaResultados.clear();
                    if (foods != null) {
                        for (FoodProduct f : foods) {
                            Map<String, Object> map = new HashMap<>();
                            map.put("nombre", f.getNombre());
                            map.put("kcal", f.getKcal());
                            map.put("rawObject", f);
                            listaResultados.add(map);
                        }
                    }
                    runOnUiThread(() -> {
                        resultadosAdapter.notifyDataSetChanged();
                        rvResultadosBusqueda.setVisibility(listaResultados.isEmpty() ? View.GONE : View.VISIBLE);
                    });
                }
            }
            @Override
            public void onFailure(Call<FoodSearchResponse> call, Throwable t) {}
        });
    }

    private void onAlimentoSeleccionadoDeBusqueda(Map<String, Object> map) {
        alimentoActual = (FoodProduct) map.get("rawObject");
        tvNombreAlimentoSeleccionado.setText(alimentoActual.getNombre());
        cardAlimentoSeleccionado.setVisibility(View.VISIBLE);
        rvResultadosBusqueda.setVisibility(View.GONE);
        etGramos.requestFocus();
        recalcularMacrosTemporales();
    }

    private void agregarIngredienteALista() {
        String gramosStr = etGramos.getText().toString();
        if (gramosStr.isEmpty() || alimentoActual == null) return;

        double gramos = Double.parseDouble(gramosStr);
        double factor = gramos / 100.0;
        
        double kcalFinal = alimentoActual.getKcal() * factor;
        double protFinal = alimentoActual.getProteinas() * factor;
        double carbFinal = alimentoActual.getCarbohidratos() * factor;
        double grasaFinal = alimentoActual.getGrasas() * factor;

        Map<String, Object> ingrediente = new HashMap<>();
        ingrediente.put("nombre", alimentoActual.getNombre());
        ingrediente.put("gramos", gramos);
        ingrediente.put("kcal", kcalFinal);
        ingrediente.put("prot", protFinal);
        ingrediente.put("carb", carbFinal);
        ingrediente.put("grasas", grasaFinal);

        listaIngredientes.add(ingrediente);
        
        totalKcal += kcalFinal;
        totalProt += protFinal;
        totalCarb += carbFinal;
        totalGrasa += grasaFinal;
        
        actualizarVistasTotales();
        ingredientesAdapter.notifyDataSetChanged();
        
        cardAlimentoSeleccionado.setVisibility(View.GONE);
        etBuscarAlimento.setText("");
        etGramos.setText("");
        alimentoActual = null;
        resetearDashboard();
    }

    private void actualizarVistasTotales() {
        tvTotalCalorias.setText(String.format(Locale.getDefault(), "Total: %.0f kcal", totalKcal));
        tvTotalMacros.setText(String.format(Locale.getDefault(), "P: %.1fg | C: %.1fg | G: %.1fg", totalProt, totalCarb, totalGrasa));
    }

    private void guardarComidaCompleta() {
        if (listaIngredientes.isEmpty()) {
            Toast.makeText(this, "Añade al menos un ingrediente", Toast.LENGTH_SHORT).show();
            return;
        }

        String uid = auth.getCurrentUser() != null ? auth.getCurrentUser().getUid() : "anonimo";
        String fecha = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(new Date());

        Map<String, Object> comida = new HashMap<>();
        comida.put("uid", uid);
        comida.put("fecha", fecha);
        comida.put("tipoComida", spinnerTipoComida.getSelectedItem().toString());
        comida.put("ingredientes", listaIngredientes);
        comida.put("calorias", totalKcal); // Mantenemos el nombre del campo por consistencia
        comida.put("proteinas", totalProt);
        comida.put("carbohidratos", totalCarb);
        comida.put("grasas", totalGrasa);

        btnGuardarComida.setEnabled(false);
        db.collection("comidas").add(comida).addOnSuccessListener(doc -> {
            Toast.makeText(this, "¡Comida guardada!", Toast.LENGTH_SHORT).show();
            finish();
        }).addOnFailureListener(e -> btnGuardarComida.setEnabled(true));
    }

    // ADAPTADORES
    private static class ResultadosAdapter extends RecyclerView.Adapter<ResultadosAdapter.VH> {
        private final List<Map<String, Object>> lista;
        private final OnClick listener;
        interface OnClick { void call(Map<String, Object> m); }
        ResultadosAdapter(List<Map<String, Object>> l, OnClick c) { this.lista = l; this.listener = c; }
        @NonNull @Override public VH onCreateViewHolder(@NonNull ViewGroup p, int t) {
            return new VH(LayoutInflater.from(p.getContext()).inflate(R.layout.item_resultado_alimento, p, false));
        }
        @Override public void onBindViewHolder(@NonNull VH h, int p) {
            Map<String, Object> m = lista.get(p);
            h.t1.setText((String) m.get("nombre"));
            h.t2.setText(String.format("%.0f kcal/100g", (Double) m.get("kcal")));
            h.itemView.setOnClickListener(v -> listener.call(m));
        }
        @Override public int getItemCount() { return lista.size(); }
        static class VH extends RecyclerView.ViewHolder {
            TextView t1, t2;
            VH(View i) { super(i); t1 = i.findViewById(R.id.tvNombreResultado); t2 = i.findViewById(R.id.tvKcalResultado); }
        }
    }

    private static class IngredientesAdapter extends RecyclerView.Adapter<IngredientesAdapter.VH> {
        private final List<Map<String, Object>> lista;
        IngredientesAdapter(List<Map<String, Object>> l) { this.lista = l; }
        @NonNull @Override public VH onCreateViewHolder(@NonNull ViewGroup p, int t) {
            View v = LayoutInflater.from(p.getContext()).inflate(android.R.layout.simple_list_item_2, p, false);
            return new VH(v);
        }
        @Override public void onBindViewHolder(@NonNull VH h, int p) {
            Map<String, Object> m = lista.get(p);
            h.t1.setText(String.format("%s (%.0fg)", m.get("nombre"), m.get("gramos")));
            h.t2.setText(String.format("%.0f kcal | P: %.1fg | C: %.1fg | G: %.1fg", 
                (Double)m.get("kcal"), (Double)m.get("prot"), (Double)m.get("carb"), (Double)m.get("grasas")));
        }
        @Override public int getItemCount() { return lista.size(); }
        static class VH extends RecyclerView.ViewHolder {
            TextView t1, t2;
            VH(View i) { super(i); t1 = i.findViewById(android.R.id.text1); t2 = i.findViewById(android.R.id.text2); }
        }
    }
}