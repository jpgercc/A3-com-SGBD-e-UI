package br.com.app.controller;

import br.com.app.model.Admin;
import br.com.app.model.Animal;
import br.com.app.model.User;

import javax.swing.JOptionPane;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AppController {
    private static List<Animal> pets = new ArrayList<>();
    private static Admin admin = new Admin("admin", "admin");

    public void startApp() {
        JOptionPane.showMessageDialog(null, "Bem-vindo ao aplicativo de adoção de animais!");

        int opcao = 0;
        do {
            String opcaoStr = JOptionPane.showInputDialog("Escolha uma opção:\n" +
                    "0 - Sair\n" +
                    "1 - Login como Administrador\n" +
                    "2 - Login como Usuário");

            opcao = Integer.parseInt(opcaoStr);

            switch (opcao) {
                case 1:
                    loginAdmin();
                    break;
                case 2:
                    loginUsuario();
                    break;
                case 0:
                    JOptionPane.showMessageDialog(null, "Obrigado por utilizar o aplicativo!");
                    break;
                default:
                    JOptionPane.showMessageDialog(null, "Opção inválida. Tente novamente.");
                    break;
            }
        } while (opcao != 0);
    }

    private void loginAdmin() {
        String login = JOptionPane.showInputDialog("Digite o login do administrador:");
        String senha = JOptionPane.showInputDialog("Digite a senha do administrador:");

        if (login.equals(admin.getLogin()) && senha.equals(admin.getSenha())) {
            JOptionPane.showMessageDialog(null, "Login realizado com sucesso como administrador!");
            menuAdmin();
        } else {
            JOptionPane.showMessageDialog(null, "Login ou senha incorretos.");
        }
    }

    private void menuAdmin() {
        int opcao = 0;
        do {
            String opcaoStr = JOptionPane.showInputDialog("Menu Administrador\n" +
                    "Escolha uma opção:\n" +
                    "0 - Voltar\n" +
                    "1 - Adicionar animal");

            opcao = Integer.parseInt(opcaoStr);

            switch (opcao) {
                case 1:
                    adicionarAnimal();
                    break;
                case 0:
                    JOptionPane.showMessageDialog(null, "Retornando ao menu principal.");
                    break;
                default:
                    JOptionPane.showMessageDialog(null, "Opção inválida. Tente novamente.");
                    break;
            }
        } while (opcao != 0);
    }

    private void adicionarAnimal() {
        String cpfDono = JOptionPane.showInputDialog("Digite o CPF do dono:");
        String especie = JOptionPane.showInputDialog("Digite a espécie do animal:");
        String raca = JOptionPane.showInputDialog("Digite a raça do animal:");
        String temperamento = JOptionPane.showInputDialog("Digite o temperamento do animal:");
        String idadeStr = JOptionPane.showInputDialog("Digite a idade do animal:");
        int idade = Integer.parseInt(idadeStr);

        try (Connection connection = getConnection()) {
            String query = "INSERT INTO animais (cpf_dono, especie, raca, temperamento, idade) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, cpfDono);
            statement.setString(2, especie);
            statement.setString(3, raca);
            statement.setString(4, temperamento);
            statement.setInt(5, idade);

            int rowsInserted = statement.executeUpdate();
            if (rowsInserted > 0) {
                JOptionPane.showMessageDialog(null, "Animal adicionado com sucesso!");
            } else {
                JOptionPane.showMessageDialog(null, "Falha ao adicionar o animal.");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao inserir o animal no banco de dados: " + e.getMessage());
        }
    }

    private void loginUsuario() {
        String resposta = JOptionPane.showInputDialog("Você já possui cadastro? (S/N)");

        if (resposta.equalsIgnoreCase("N")) {
            String novoLogin = JOptionPane.showInputDialog("Digite o novo login:");
            String novaSenha = JOptionPane.showInputDialog("Digite a nova senha:");

            User usuario = new User(novoLogin, novaSenha);
            if (salvarUsuario(usuario)) {
                JOptionPane.showMessageDialog(null, "Cadastro realizado com sucesso! Faça o login com as suas credenciais.");
            } else {
                JOptionPane.showMessageDialog(null, "Falha ao cadastrar o usuário.");
            }
        } else if (resposta.equalsIgnoreCase("S")) {
            String login = JOptionPane.showInputDialog("Digite o login:");
            String senha = JOptionPane.showInputDialog("Digite a senha:");

            if (verificarCredenciaisUsuario(login, senha)) {
                JOptionPane.showMessageDialog(null, "Login realizado com sucesso como usuário!");
                listarPetsDisponiveis();
            } else {
                JOptionPane.showMessageDialog(null, "Login ou senha incorretos.");
            }
        } else {
            JOptionPane.showMessageDialog(null, "Opção inválida.");
        }
    }

    private boolean verificarCredenciaisUsuario(String login, String senha) {
        try (Connection connection = getConnection()) {
            String query = "SELECT * FROM usuarios WHERE login = ? AND senha = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, login);
            statement.setString(2, senha);

            ResultSet resultSet = statement.executeQuery();
            return resultSet.next(); // Retorna verdadeiro se houver um resultado
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao verificar as credenciais do usuário: " + e.getMessage());
            return false;
        }
    }

    private boolean salvarUsuario(User usuario) {
        try (Connection connection = getConnection()) {
            String query = "INSERT INTO usuarios (login, senha) VALUES (?, ?)";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, usuario.getLogin());
            statement.setString(2, usuario.getSenha());

            int rowsInserted = statement.executeUpdate();
            return rowsInserted > 0;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao salvar o usuário no banco de dados: " + e.getMessage());
            return false;
        }
    }

    private void listarPetsDisponiveis() {
        try (Connection connection = getConnection()) {
            String query = "SELECT cpf_dono, especie, raca, temperamento, idade FROM animais";
            PreparedStatement statement = connection.prepareStatement(query);

            ResultSet resultSet = statement.executeQuery();
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("\nLista de Pets Disponíveis para Adoção:\n");
            while (resultSet.next()) {
                String cpfDono = resultSet.getString("cpf_dono");
                String especie = resultSet.getString("especie");
                String raca = resultSet.getString("raca");
                String temperamento = resultSet.getString("temperamento");
                int idade = resultSet.getInt("idade");

                stringBuilder.append("CPF do dono: ").append(cpfDono).append("\n");
                stringBuilder.append("Espécie: ").append(especie).append("\n");
                stringBuilder.append("Raça: ").append(raca).append("\n");
                stringBuilder.append("Temperamento: ").append(temperamento).append("\n");
                stringBuilder.append("Idade: ").append(idade).append("\n");
                stringBuilder.append("---------------------------------------------------------\n");
            }
            stringBuilder.append("Contate 991625320 para adotar seu novo pet! \n");
            stringBuilder.append("Adoção somente depois do contato e avaliação.");
            JOptionPane.showMessageDialog(null, stringBuilder.toString());
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao listar os pets disponíveis: " + e.getMessage());
        }
    }

    private Connection getConnection() throws SQLException {
        String url = "YOURDBURL"; // Substitua pelo URL do seu banco de dados
        String user = "YOURUSER"; // Substitua pelo nome de usuário do seu banco de dados
        String password = "YOURPASSWORD"; // Substitua pela senha do seu banco de dados

        return DriverManager.getConnection(url, user, password);
    }
}
