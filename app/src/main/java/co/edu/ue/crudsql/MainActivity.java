package co.edu.ue.crudsql;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

import co.edu.ue.crudsql.entities.User;
import co.edu.ue.crudsql.repository.UserRepository;

public class MainActivity extends AppCompatActivity {

    private Context context;
    private EditText etDocumento, etUsuario, etNombres, etApellidos, etContraseña;
    private ListView listUsers;
    private Button btnGuardar, btnBuscar, btnListar, btnBorrar;
    private User selectedUser; // Variable para almacenar el usuario seleccionado
    private SQLiteDatabase sqliteDatabase;
    private int documento;
    private String usuario;
    private String nombres;
    private String apellidos;
    private String pass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        begin();
        btnGuardar.setOnClickListener(this::createOrUpdateUser);
        btnBuscar.setOnClickListener(this::searchUser);
        btnListar.setOnClickListener(this::listAllUsers);
        btnBorrar.setOnClickListener(this::deleteUser);

        // Configurar selección en la lista (permite seleccionar usuarios para editar/borrar)
        listUsers.setOnItemClickListener((parent, view, position, id) -> {
            selectedUser = (User) parent.getItemAtPosition(position);
            loadUserData(selectedUser);
        });
    }

    // Metodo para cargar datos del usuario seleccionado en los EditText
    private void loadUserData(User user) {
        etDocumento.setText(String.valueOf(user.getDocument()));
        etUsuario.setText(user.getUser());
        etNombres.setText(user.getName());
        etApellidos.setText(user.getLastName());
        etContraseña.setText(user.getPass());
        btnGuardar.setText(R.string.btnActualizar);
    }

    // Metodo para listar usuarios
    private void listUser(View view) {
        UserRepository userRepository = new UserRepository(context, view);
        ArrayList<User> list = userRepository.getUserList();
        ArrayAdapter<User> arrayAdapter = new ArrayAdapter<>
                (context, android.R.layout.simple_list_item_1, list) {
            @SuppressLint("SetTextI18n")
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                User user = getItem(position);
                TextView textView = (TextView) view;
                // MEJORADO: Mostrar solo información relevante (sin contraseña)
                textView.setText(user.getDocument() + " - " + user.getName() + " " + user.getLastName());
                return view;
            }
        };
        this.listUsers.setAdapter(arrayAdapter);
    }

    // Para listar todos los usuarios (similar a listUser pero con nombre más descriptivo)
    private void listAllUsers(View view) {
        listUser(view); // Reutiliza la función existente
        clearSelection(); // Limpia la selección al listar todos
    }

    // Buscar usuario por documento específico
    private void searchUser(View view) {
        if (etDocumento.getText().toString().isEmpty()) {
            Toast.makeText(context, "Ingrese un documento", Toast.LENGTH_SHORT).show();
            return;
        }

        int document = Integer.parseInt(etDocumento.getText().toString());
        UserRepository userRepository = new UserRepository(context, view);
        User user = userRepository.getUserByDocument(document);

        if (user != null) {
            selectedUser = user;
            loadUserData(user);
            // Mostrar solo el usuario encontrado
            ArrayList<User> singleUserList = new ArrayList<>();
            singleUserList.add(user);
            ArrayAdapter<User> adapter = new ArrayAdapter<>(context,
                    android.R.layout.simple_list_item_1, singleUserList);
            listUsers.setAdapter(adapter);
        } else {
            Toast.makeText(context, "Usuario no encontrado", Toast.LENGTH_SHORT).show();
        }
    }

    // Crear o actualizar usuario (reemplaza a createUser)
    private void createOrUpdateUser(View view) {
        if (!validateFields()) return;

        capData();
        User user = new User(documento, nombres, apellidos, usuario, pass);
        UserRepository userRepository = new UserRepository(context, view);

        if (selectedUser == null) {
            // Crear nuevo usuario
            userRepository.insertUser(user);
            Toast.makeText(context, "Usuario creado", Toast.LENGTH_SHORT).show();
        } else {
            // Actualizar usuario existente
            if (userRepository.updateUser(user)) {
                Toast.makeText(context, "Usuario actualizado", Toast.LENGTH_SHORT).show();
            }
        }

        clearFields();
        listAllUsers(view);
    }

    // Eliminar usuario seleccionado
    private void deleteUser(View view) {
        if (selectedUser == null) {
            Toast.makeText(context, "Seleccione un usuario primero", Toast.LENGTH_SHORT).show();
            return;
        }

        UserRepository userRepository = new UserRepository(context, view);
        if (userRepository.deleteUser(selectedUser.getDocument())) {
            Toast.makeText(context, "Usuario eliminado", Toast.LENGTH_SHORT).show();
            clearFields();
            listAllUsers(view);
        }
    }

    // Capturar datos de los campos
    private void capData() {
        this.documento = Integer.parseInt(this.etDocumento.getText().toString());
        this.usuario = etUsuario.getText().toString();
        this.nombres = etNombres.getText().toString();
        this.apellidos = etApellidos.getText().toString();
        this.pass = etContraseña.getText().toString();
    }

    // Validar campos obligatorios
    private boolean validateFields() {
        if (etDocumento.getText().toString().isEmpty() ||
                etNombres.getText().toString().isEmpty() ||
                etApellidos.getText().toString().isEmpty() ||
                etUsuario.getText().toString().isEmpty() ||
                etContraseña.getText().toString().isEmpty()) {
            Toast.makeText(context, "Complete todos los campos", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    // Limpiar campos y selección
    private void clearFields() {
        etDocumento.setText("");
        etUsuario.setText("");
        etNombres.setText("");
        etApellidos.setText("");
        etContraseña.setText("");
        selectedUser = null;
        btnGuardar.setText(R.string.btnRegistrar);
    }

    // Limpiar selección
    private void clearSelection() {
        selectedUser = null;
    }

    // Inicializar vistas
    private void begin() {
        this.etNombres = findViewById(R.id.etNombres);
        this.etApellidos = findViewById(R.id.etApellidos);
        this.etContraseña = findViewById(R.id.etContraseña);
        this.etUsuario = findViewById(R.id.etUsuario);
        this.etDocumento = findViewById(R.id.etDocumento);
        this.btnGuardar = findViewById(R.id.btnGuardar);
        this.btnBuscar = findViewById(R.id.btnBuscar);
        this.btnListar = findViewById(R.id.btnListar);
        this.btnBorrar = findViewById(R.id.btnBorrar);
        this.listUsers = findViewById(R.id.listUsers);
        this.context = this;
    }
}