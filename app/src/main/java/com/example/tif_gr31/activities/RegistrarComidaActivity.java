package com.example.tif_gr31.activities;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tif_gr31.R;
import com.example.tif_gr31.api.ApiClient;
import com.example.tif_gr31.api.FoodProduct;
import com.example.tif_gr31.api.FoodSearchResponse;
import com.example.tif_gr31.utils.FloatingNavigationHelper;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegistrarComidaActivity extends AppCompatActivity {

    private static final String TAG = "RegistrarComidaAct";
    private static final String USDA_API_KEY = "BpuqGxfs81r5NeHjS2pe7fdWV2fRJY5vKd59lgnI";
    private static final String[] TIPOS_COMIDA = {"Desayuno", "Almuerzo", "Merienda", "Cena", "Snacks"};

    private EditText etBuscarAlimento, etGramos;
    private ImageView btnLimpiarBusqueda;
    private RecyclerView rvResultadosBusqueda, rvIngredientesAgregados;
    private View cardAlimentoSeleccionado;
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
        Log.d(TAG, "onCreate: Iniciando actividad de registro");
        
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registrar_comida);

        View mainView = findViewById(R.id.registrarComidaMain);
        if (mainView != null) {
            ViewCompat.setOnApplyWindowInsetsListener(mainView, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        }

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        vincularVistas();
        configurarAdapters();
        configurarBusqueda();
        configurarSpinner();
        configurarCampoGramos();

        manejarCategoriaPreseleccionada();

        // Configuración de Navegación Flotante (sin activar ningún ítem)
        FloatingNavigationHelper.setupFloatingNavigation(this, -1);

        if (btnAgregarIngrediente != null) {
            btnAgregarIngrediente.setOnClickListener(v -> agregarIngredienteALista());
        }
        if (btnGuardarComida != null) {
            btnGuardarComida.setOnClickListener(v -> guardarComidaCompleta());
        }
        
        View btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }
    }

    private void manejarCategoriaPreseleccionada() {
        String categoria = getIntent().getStringExtra("CATEGORIA_SELECCIONADA");
        if (categoria != null && spinnerTipoComida != null) {
            int position = Arrays.asList(TIPOS_COMIDA).indexOf(categoria);
            if (position >= 0) {
                spinnerTipoComida.setSelection(position);
            }
        }
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
    }

    private void configurarAdapters() {
        if (rvResultadosBusqueda != null) {
            rvResultadosBusqueda.setLayoutManager(new LinearLayoutManager(this));
            resultadosAdapter = new ResultadosAdapter(listaResultados, this::onAlimentoSeleccionadoDeBusqueda);
            rvResultadosBusqueda.setAdapter(resultadosAdapter);
        }

        if (rvIngredientesAgregados != null) {
            rvIngredientesAgregados.setLayoutManager(new LinearLayoutManager(this));
            ingredientesAdapter = new IngredientesAdapter(listaIngredientes);
            rvIngredientesAgregados.setAdapter(ingredientesAdapter);
        }
    }

    private void configurarBusqueda() {
        if (etBuscarAlimento == null) return;
        
        etBuscarAlimento.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                String query = s.toString().trim();
                if (btnLimpiarBusqueda != null) {
                    btnLimpiarBusqueda.setVisibility(query.isEmpty() ? View.GONE : View.VISIBLE);
                }
                if (debouncedSearch != null) debounceHandler.removeCallbacks(debouncedSearch);
                if (query.length() < 2) {
                    if (rvResultadosBusqueda != null) rvResultadosBusqueda.setVisibility(View.GONE);
                    return;
                }
                debouncedSearch = () -> buscarEnUSDA(query);
                debounceHandler.postDelayed(debouncedSearch, 600);
            }
        });

        if (btnLimpiarBusqueda != null) {
            btnLimpiarBusqueda.setOnClickListener(v -> {
                etBuscarAlimento.setText("");
                if (rvResultadosBusqueda != null) rvResultadosBusqueda.setVisibility(View.GONE);
            });
        }
    }

    private void configurarCampoGramos() {
        if (etGramos == null) return;
        etGramos.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                recalcularMacrosTemporales();
            }
        });
    }

    private void recalcularMacrosTemporales() {
        if (alimentoActual == null || etGramos == null) return;
        String gramosStr = etGramos.getText().toString().trim();
        if (gramosStr.isEmpty()) {
            resetearDashboard();
            return;
        }

        try {
            double gramos = Double.parseDouble(gramosStr);
            double factor = gramos / 100.0;
            if (tvCaloriasCalculadas != null) tvCaloriasCalculadas.setText(String.format(Locale.getDefault(), "%.0f", alimentoActual.getKcal() * factor));
            if (tvProteinasCalculadas != null) tvProteinasCalculadas.setText(String.format(Locale.getDefault(), "%.1f", alimentoActual.getProteinas() * factor));
            if (tvCarbosCalculados != null) tvCarbosCalculados.setText(String.format(Locale.getDefault(), "%.1f", alimentoActual.getCarbohidratos() * factor));
            if (tvGrasasCalculadas != null) tvGrasasCalculadas.setText(String.format(Locale.getDefault(), "%.1f", alimentoActual.getGrasas() * factor));
        } catch (Exception e) {
            resetearDashboard();
        }
    }

    private void resetearDashboard() {
        if (tvCaloriasCalculadas != null) tvCaloriasCalculadas.setText("0");
        if (tvProteinasCalculadas != null) tvProteinasCalculadas.setText("0");
        if (tvCarbosCalculados != null) tvCarbosCalculados.setText("0");
        if (tvGrasasCalculadas != null) tvGrasasCalculadas.setText("0");
    }

    private void configurarSpinner() {
        if (spinnerTipoComida == null) return;
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
                        if (resultadosAdapter != null) resultadosAdapter.notifyDataSetChanged();
                        if (rvResultadosBusqueda != null) rvResultadosBusqueda.setVisibility(listaResultados.isEmpty() ? View.GONE : View.VISIBLE);
                    });
                }
            }
            @Override
            public void onFailure(Call<FoodSearchResponse> call, Throwable t) {
                Log.e(TAG, "onFailure: Error en API USDA", t);
            }
        });
    }

    private void onAlimentoSeleccionadoDeBusqueda(Map<String, Object> map) {
        alimentoActual = (FoodProduct) map.get("rawObject");
        if (tvNombreAlimentoSeleccionado != null) tvNombreAlimentoSeleccionado.setText(alimentoActual.getNombre());
        if (cardAlimentoSeleccionado != null) cardAlimentoSeleccionado.setVisibility(View.VISIBLE);
        if (rvResultadosBusqueda != null) rvResultadosBusqueda.setVisibility(View.GONE);
        if (etGramos != null) {
            etGramos.requestFocus();
        }
        recalcularMacrosTemporales();
    }

    private void agregarIngredienteALista() {
        if (etGramos == null || alimentoActual == null) return;
        String gramosStr = etGramos.getText().toString();
        if (gramosStr.isEmpty()) {
            Toast.makeText(this, "Ingresa los gramos", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
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

            actualizarResumenTotales();
            if (ingredientesAdapter != null) ingredientesAdapter.notifyDataSetChanged();

            // Limpiar para el siguiente ingrediente
            alimentoActual = null;
            if (cardAlimentoSeleccionado != null) cardAlimentoSeleccionado.setVisibility(View.GONE);
            if (etBuscarAlimento != null) etBuscarAlimento.setText("");
            etGramos.setText("");
        } catch (Exception e) {
            Toast.makeText(this, "Cantidad inválida", Toast.LENGTH_SHORT).show();
        }
    }

    private void actualizarResumenTotales() {
        if (tvTotalCalorias != null) tvTotalCalorias.setText(String.format(Locale.getDefault(), "Total: %.0f kcal", totalKcal));
        if (tvTotalMacros != null) tvTotalMacros.setText(String.format(Locale.getDefault(), "P: %.1fg | C: %.1fg | G: %.1fg", totalProt, totalCarb, totalGrasa));
    }

    private void guardarComidaCompleta() {
        if (listaIngredientes.isEmpty()) {
            Toast.makeText(this, "Añade al menos un ingrediente", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = auth.getUid();
        if (userId == null) {
            Toast.makeText(this, "Usuario no autenticado", Toast.LENGTH_SHORT).show();
            return;
        }

        String tipoComida = (spinnerTipoComida != null) ? spinnerTipoComida.getSelectedItem().toString() : "Otro";
        String fechaHoy = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        Map<String, Object> comidaMap = new HashMap<>();
        comidaMap.put("userId", userId);
        comidaMap.put("tipo", tipoComida);
        comidaMap.put("fecha", fechaHoy);
        comidaMap.put("timestamp", new Date());
        comidaMap.put("totalKcal", totalKcal);
        comidaMap.put("totalProt", totalProt);
        comidaMap.put("totalCarb", totalCarb);
        comidaMap.put("totalGrasa", totalGrasa);
        comidaMap.put("ingredientes", listaIngredientes);

        db.collection("usuarios").document(userId).collection("comidas").add(comidaMap)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Comida guardada exitosamente", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "guardarComidaCompleta: Error al guardar", e);
                    Toast.makeText(this, "Error al guardar", Toast.LENGTH_SHORT).show();
                });
    }

    // --- ADAPTERS INTERNOS ---

    private class ResultadosAdapter extends RecyclerView.Adapter<ResultadosAdapter.ViewHolder> {
        private final List<Map<String, Object>> items;
        private final OnItemClickListener listener;

        public ResultadosAdapter(List<Map<String, Object>> items, OnItemClickListener listener) {
            this.items = items;
            this.listener = listener;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_resultado_alimento, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Map<String, Object> item = items.get(position);
            String nombre = (String) item.get("nombre");
            Double kcal = (Double) item.get("kcal");
            
            holder.tvNombre.setText(nombre != null ? nombre : "Desconocido");
            holder.tvKcal.setText(String.format(Locale.getDefault(), "%.0f kcal / 100g", kcal != null ? kcal : 0.0));
            holder.itemView.setOnClickListener(v -> listener.onItemClick(item));
        }

        @Override public int getItemCount() { return items.size(); }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvNombre, tvKcal;
            ViewHolder(View v) {
                super(v);
                tvNombre = v.findViewById(R.id.tvNombreResultado);
                tvKcal = v.findViewById(R.id.tvKcalResultado);
            }
        }
    }

    private class IngredientesAdapter extends RecyclerView.Adapter<IngredientesAdapter.ViewHolder> {
        private final List<Map<String, Object>> items;
        public IngredientesAdapter(List<Map<String, Object>> items) { this.items = items; }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_2, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Map<String, Object> item = items.get(position);
            holder.t1.setText(String.format("%s (%.0fg)", item.get("nombre"), (Double) item.get("gramos")));
            holder.t2.setText(String.format(Locale.getDefault(), "%.1f kcal | P: %.1fg | C: %.1fg | G: %.1fg", 
                    (Double) item.get("kcal"), (Double) item.get("prot"), (Double) item.get("carb"), (Double) item.get("grasas")));
        }

        @Override public int getItemCount() { return items.size(); }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView t1, t2;
            ViewHolder(View v) {
                super(v);
                t1 = v.findViewById(android.R.id.text1);
                t2 = v.findViewById(android.R.id.text2);
            }
        }
    }

    interface OnItemClickListener {
        void onItemClick(Map<String, Object> item);
    }
}
