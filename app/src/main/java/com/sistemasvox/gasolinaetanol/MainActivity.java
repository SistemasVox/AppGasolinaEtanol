package com.sistemasvox.gasolinaetanol;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    // Elementos do layout
    private EditText editTextTeorGasolina;
    private EditText editTextPropocaoGasolina;
    private EditText editTextTotalLitros;
    private TextView textViewResultadoGasolina;
    private TextView textViewResultadoEtanol;
    private TextView textViewResultadoCustoGasolina;
    private TextView textViewResultadoCustoEtanol;
    private TextView textViewResultadoCustoTotal;
    private TextView textViewCompensaEtanol;
    private EditText editTextPrecoGasolina;
    private EditText editTextPrecoEtanol;
    Button btnCalcular;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Vincula elementos do layout às variáveis Java
        editTextTeorGasolina = findViewById(R.id.editTextTeorGasolina);
        editTextPropocaoGasolina = findViewById(R.id.editTextPropocaoGasolina);
        editTextTotalLitros = findViewById(R.id.editTextTotalLitros);
        editTextPrecoGasolina = findViewById(R.id.editTextPrecoGasolina);
        editTextPrecoEtanol = findViewById(R.id.editTextPrecoEtanol);
        btnCalcular = findViewById(R.id.buttonCalcular);
        textViewResultadoGasolina = findViewById(R.id.textViewResultadoGasolina);
        textViewResultadoEtanol = findViewById(R.id.textViewResultadoEtanol);
        textViewResultadoCustoGasolina = findViewById(R.id.textViewResultadoCustoGasolina);
        textViewResultadoCustoEtanol = findViewById(R.id.textViewResultadoCustoEtanol);
        textViewResultadoCustoTotal = findViewById(R.id.textViewResultadoCustoTotal);
        textViewCompensaEtanol = findViewById(R.id.textViewCompensaEtanol);

        // Define a ação do botão ao ser clicado
        btnCalcular.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Chama a função de cálculo
                calcularCombustivel();
            }
        });
    }

    // Função para realizar os cálculos
    private void calcularCombustivel() {
        // Verifica se os campos de entrada não estão vazios
        if (editTextTeorGasolina.getText().toString().isEmpty() ||
                editTextTotalLitros.getText().toString().isEmpty() ||
                editTextPrecoGasolina.getText().toString().isEmpty() ||
                editTextPrecoEtanol.getText().toString().isEmpty()) {
            // Exibe uma mensagem de erro se algum campo estiver vazio
            showToast("Preencha todos os campos de entrada.");
            return;
        }

        try {
            // Obtém os valores dos EditTexts
            float teorGasolina = Float.parseFloat(editTextTeorGasolina.getText().toString());
            float proporcaoGasolinaDesejada = Float.parseFloat(editTextPropocaoGasolina.getText().toString());
            float totalLitros = Float.parseFloat(editTextTotalLitros.getText().toString());
            float precoGasolina = Float.parseFloat(editTextPrecoGasolina.getText().toString());
            float precoEtanol = Float.parseFloat(editTextPrecoEtanol.getText().toString());

            // Inicializando os limites para a busca binária
            float limiteInferior = 0;
            float limiteSuperior = totalLitros;
            float tolerancia = 0.01f; // Tolerância para a proporção desejada

            float volumeGasolina = 0, volumeEtanol = 0, proporcaoGasolinaPura;

            // Realizando a busca binária
            while (limiteSuperior - limiteInferior > tolerancia) {
                volumeGasolina = (limiteInferior + limiteSuperior) / 2;
                volumeEtanol = totalLitros - volumeGasolina;

                proporcaoGasolinaPura = (volumeGasolina * (1 - teorGasolina)) / (volumeGasolina + volumeEtanol);

                if (proporcaoGasolinaPura < proporcaoGasolinaDesejada) {
                    limiteInferior = volumeGasolina;
                } else {
                    limiteSuperior = volumeGasolina;
                }
            }

            // Calcula o custo em reais
            float custoGasolina = volumeGasolina * precoGasolina;
            float custoEtanol = volumeEtanol * precoEtanol;
            float custoTotal = custoGasolina + custoEtanol;

            // Exibe os resultados nos TextViews
            textViewResultadoGasolina.setText(getString(R.string.quantidade_gasolina, String.format(Locale.getDefault(), "%.2f", volumeGasolina)));
            textViewResultadoEtanol.setText(getString(R.string.quantidade_etanol, String.format(Locale.getDefault(), "%.2f", volumeEtanol)));
            textViewResultadoCustoGasolina.setText(getString(R.string.custo_gasolina, String.format(Locale.getDefault(), "R$ %.2f", custoGasolina)));
            textViewResultadoCustoEtanol.setText(getString(R.string.custo_etanol, String.format(Locale.getDefault(), "R$ %.2f", custoEtanol)));
            textViewResultadoCustoTotal.setText(getString(R.string.custo_total, String.format(Locale.getDefault(), "R$ %.2f", custoTotal)));

            // Calcula a porcentagem do preço do etanol em relação ao preço da gasolina
            float porcentagemEtanol = (precoEtanol / precoGasolina) * 100;

            // Verifica se compensa usar etanol
            boolean compensaEtanol = precoEtanol < precoGasolina * 0.7;
            String mensagem = compensaEtanol ? "Compensa utilizar etanol." : "Não compensa utilizar etanol.";
            mensagem += String.format(Locale.getDefault(), " (Etanol é %.2f%% do preço da gasolina)", porcentagemEtanol);

            textViewCompensaEtanol.setText(mensagem);


        } catch (NumberFormatException e) {
            // Exibe uma mensagem de erro se houver problemas na conversão de string para float
            showToast("Erro ao converter valores. Certifique-se de inserir números válidos.");
        }
    }

    // Função auxiliar para exibir mensagens de toast
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

}
