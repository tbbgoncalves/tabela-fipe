package br.com.alura.tabelafipe.main;

import br.com.alura.tabelafipe.model.*;
import br.com.alura.tabelafipe.service.ConversorDados;
import br.com.alura.tabelafipe.service.ConsumoApi;

import java.util.*;
import java.util.stream.Collectors;

public class Principal {
    private Scanner leitura = new Scanner(System.in);
    private final String URL_BASE = "https://parallelum.com.br/fipe/api/v1/";
    private String urlRequisicao;
    private String opcao;
    private String tipoVeiculoBuscado;
    private String codigoMarcaBuscada;
    private String codigoModeloBuscado;
    private ConsumoApi consumoApi = new ConsumoApi();
    private ConversorDados conversorDados = new ConversorDados();

    public void exibirMenu() {
        //solicita o tipo de veículo ao usuário
        System.out.println("carros, motos ou caminhoes");
        System.out.println("Digite o tipo de veículo desejado:");
        opcao = leitura.nextLine();

        //validação do que foi digitado
        if(opcao.toLowerCase().contains("carr")) {
            tipoVeiculoBuscado = "carros";
        } else if(opcao.toLowerCase().contains("mot")) {
            tipoVeiculoBuscado = "motos";
        } else if(opcao.toLowerCase().contains("caminh")) {
            tipoVeiculoBuscado = "caminhoes";
        } else {
            throw new RuntimeException("Opção inválida");
        }

        //faz a requisição da lista de marcas
        urlRequisicao = URL_BASE + tipoVeiculoBuscado + "/marcas/";
        String json = consumoApi.pegarDados(urlRequisicao);
        List<Dados> marcas = conversorDados.pegarLista(json, Dados.class);

        //exibe a lista de marcas e solicita o código da marca desejada
        marcas.forEach(System.out::println);
        System.out.println("Digite o código da marca: ");
        codigoMarcaBuscada = leitura.nextLine();

        //valida se o código digitado existe
        Optional<Dados> marcaBuscada = marcas.stream()
                .filter(m -> codigoMarcaBuscada.equals(m.codigo()))
                .findFirst();
        if(marcaBuscada.isEmpty()) {
            throw new RuntimeException("Código da marca inexistente");
        }

        //faz a requisição da lista de modelos
        json = consumoApi.pegarDados(urlRequisicao += codigoMarcaBuscada + "/modelos/");
        Modelos modelos = conversorDados.pegarDados(json, Modelos.class);

        //exibe a lista de modelos e solicita o nome do modelo desejado
        modelos.listaModelos().forEach(System.out::println);
        System.out.println("Digite o nome de um modelo:");
        String nomeModeloBuscado = leitura.nextLine();
        List<Dados> modelosFiltrados = modelos.listaModelos().stream()
                .filter(m -> m.nome().toLowerCase().contains(nomeModeloBuscado.toUpperCase()))
                .collect(Collectors.toList());

        //valida se o nome do modelo existe
        if(modelosFiltrados.isEmpty()) {
            throw new RuntimeException("Modelo inexistente");
        }

        //exibe a lista dos modelos, de acordo com o nome, e solicita o código do modelo desejado
        modelosFiltrados.forEach(System.out::println);
        System.out.println("Digite o código do modelo:");
        codigoModeloBuscado = leitura.nextLine();

        //valida se o código digitado existe
        Optional<Dados> modelo = modelosFiltrados.stream()
                .filter(m -> m.codigo().equals(codigoModeloBuscado))
                .findFirst();
        if(modelo.isEmpty()) {
            throw new RuntimeException("Código do modelo inexistente");
        }

        //faz a requisição da lista de anos do modelo
        json = consumoApi.pegarDados(urlRequisicao += codigoModeloBuscado + "/anos/");
        List<Dados> anos = conversorDados.pegarLista(json, Dados.class);

        //percorre a lista de anos, instanciando a classe Veiculo, e apresenta ao usuário
        List<Veiculo> veiculos = new ArrayList<>();
        for(Dados ano: anos) {
            json = consumoApi.pegarDados(urlRequisicao + ano.codigo() + "/");
            veiculos.add(conversorDados.pegarDados(json, Veiculo.class));
        }
        veiculos.forEach(System.out::println);
    }
}
