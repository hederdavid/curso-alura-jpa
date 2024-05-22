package br.com.alura.screenmatch.service;

import com.theokanning.openai.OpenAiHttpException;
import com.theokanning.openai.completion.CompletionRequest;
import com.theokanning.openai.service.OpenAiService;
import retrofit2.HttpException;
import org.springframework.stereotype.Service;

@Service
public class ConsultaChatGPT {

    private static OpenAiService service = null;

    public ConsultaChatGPT() {
        service = new OpenAiService("sk-proj-jJS5VD0UP5Nh1uQuggChT3BlbkFJsqfhHshGyXvgMO8FiqLu");
    }

    public static String obterTraducao(String texto) {
        CompletionRequest requisicao = CompletionRequest.builder()
                .model("gpt-3.5-turbo-instruct")
                .prompt("traduza para o português o texto: " + texto)
                .maxTokens(1000)
                .temperature(0.7)
                .build();

        try {
            var resposta = service.createCompletion(requisicao);
            return resposta.getChoices().get(0).getText().trim();
        } catch (OpenAiHttpException e) {
            if (e.statusCode == 429) { // Ajuste de acordo com a estrutura da exceção
                // Lógica de espera antes de tentar novamente
                try {
                    System.err.println("Limite de requisições excedido. Aguardando antes de tentar novamente...");
                    Thread.sleep(100); // Espera por 10 segundos
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    return "Interrupção durante a espera. Tente novamente mais tarde.";
                }
                return "Excedeu o limite de requisições, tente novamente mais tarde.";
            }
            throw e; // Re-lançar outras exceções
        } catch (HttpException e) {
            if (e.code() == 429) { // Para outras exceções HTTP se a estrutura for diferente
                // Lógica de espera antes de tentar novamente
                try {
                    System.err.println("Limite de requisições excedido. Aguardando antes de tentar novamente...");
                    Thread.sleep(10000); // Espera por 10 segundos
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    return "Interrupção durante a espera. Tente novamente mais tarde.";
                }
                return "Excedeu o limite de requisições, tente novamente mais tarde.";
            }
            throw e; // Re-lançar outras exceções
        }
    }
}
