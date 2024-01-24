package br.com.alura.tabelafipe.service;

import java.util.List;

public interface IConversorDados {
    <T> T pegarDados(String json, Class<T> classe);
    <T> List<T> pegarLista(String json, Class<T> classe);
}
