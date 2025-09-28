package com.koma.androidfirebase;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.List;

public class DataFragment extends Fragment {

    private EditText etNombre, etCorreo, etMensaje;
    private Button btnRegistrar, btnListar, btnCerrarSesion;
    private ProgressBar progressBar;
    private TextView tvMensaje;
    private RecyclerView recyclerViewDatos;

    private FirebaseAuth auth;
    private DatabaseReference dataReference;
    private DataAdapter dataAdapter;
    private List<DataModel> dataList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflar el layout sin View Binding
        View view = inflater.inflate(R.layout.fragment_data, container, false);

        // Inicializar vistas con findViewById
        etNombre = view.findViewById(R.id.etNombre);
        etCorreo = view.findViewById(R.id.etCorreo);
        etMensaje = view.findViewById(R.id.etMensaje);
        btnRegistrar = view.findViewById(R.id.btnRegistrar);
        btnListar = view.findViewById(R.id.btnListar);
        btnCerrarSesion = view.findViewById(R.id.btnCerrarSesion);
        progressBar = view.findViewById(R.id.progressBar);
        tvMensaje = view.findViewById(R.id.tvMensaje);
        recyclerViewDatos = view.findViewById(R.id.recyclerViewDatos);

        auth = FirebaseAuth.getInstance();
        dataReference = FirebaseDatabase.getInstance().getReference("datos");
        dataList = new ArrayList<>();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupRecyclerView();
        setupClickListeners();
        checkAuthentication();
    }

    private void setupRecyclerView() {
        dataAdapter = new DataAdapter(dataList);
        recyclerViewDatos.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewDatos.setAdapter(dataAdapter);
    }

    private void setupClickListeners() {
        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registrarDatos();
            }
        });

        btnListar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listarDatos();
            }
        });

        btnCerrarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cerrarSesion();
            }
        });
    }

    private void checkAuthentication() {
        if (auth.getCurrentUser() == null) {
            Navigation.findNavController(requireView())
                    .navigate(R.id.action_dataFragment_to_loginFragment);
        }
    }

    private void registrarDatos() {
        String nombre = etNombre.getText().toString().trim();
        String correo = etCorreo.getText().toString().trim();
        String mensaje = etMensaje.getText().toString().trim();

        if (nombre.isEmpty() || correo.isEmpty() || mensaje.isEmpty()) {
            tvMensaje.setText("Por favor, complete todos los campos");
            return;
        }

        showLoading(true);

        String userId = auth.getCurrentUser().getUid();
        String dataId = dataReference.child(userId).push().getKey();

        DataModel data = new DataModel(dataId, nombre, correo, mensaje);

        dataReference.child(userId).child(dataId).setValue(data)
                .addOnCompleteListener(new com.google.android.gms.tasks.OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull com.google.android.gms.tasks.Task<Void> task) {
                        showLoading(false);
                        if (task.isSuccessful()) {
                            tvMensaje.setText("Datos registrados exitosamente");
                            clearInputs();
                        } else {
                            tvMensaje.setText("Error al registrar datos");
                        }
                    }
                });
    }

    private void listarDatos() {
        String userId = auth.getCurrentUser().getUid();

        showLoading(true);
        recyclerViewDatos.setVisibility(View.VISIBLE);

        dataReference.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                showLoading(false);
                List<DataModel> newDataList = new ArrayList<>();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    DataModel data = dataSnapshot.getValue(DataModel.class);
                    if (data != null) {
                        newDataList.add(data);
                    }
                }

                dataAdapter.updateData(newDataList);
                tvMensaje.setText("Datos encontrados: " + newDataList.size());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                showLoading(false);
                tvMensaje.setText("Error al cargar datos: " + error.getMessage());
            }
        });
    }

    private void cerrarSesion() {
        auth.signOut();
        Navigation.findNavController(requireView())
                .navigate(R.id.action_dataFragment_to_loginFragment);
    }

    private void clearInputs() {
        etNombre.getText().clear();
        etCorreo.getText().clear();
        etMensaje.getText().clear();
    }

    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        btnRegistrar.setEnabled(!show);
        btnListar.setEnabled(!show);
    }
}