package br.com.app;

public class Animal {
    private String cpfDono;
    private String especie;
    private String raca;
    private String temperamento;
    private int idade;

    public Animal(String cpfDono, String especie, String raca, String temperamento, int idade) {
        this.cpfDono = cpfDono;
        this.especie = especie;
        this.raca = raca;
        this.temperamento = temperamento;
        this.idade = idade;
    }

    public String getCpfDono() {
        return cpfDono;
    }

    public void setCpfDono(String cpfDono) {
        this.cpfDono = cpfDono;
    }

    public String getEspecie() {
        return especie;
    }

    public void setEspecie(String especie) {
        this.especie = especie;
    }

    public String getRaca() {
        return raca;
    }

    public void setRaca(String raca) {
        this.raca = raca;
    }

    public String getTemperamento() {
        return temperamento;
    }

    public void setTemperamento(String temperamento) {
        this.temperamento = temperamento;
    }

    public int getIdade() {
        return idade;
    }

    public void setIdade(int idade) {
        this.idade = idade;
    }
}