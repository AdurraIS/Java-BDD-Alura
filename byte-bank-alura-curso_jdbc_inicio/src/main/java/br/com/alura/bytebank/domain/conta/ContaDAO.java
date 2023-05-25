package br.com.alura.bytebank.domain.conta;

import br.com.alura.bytebank.domain.cliente.Cliente;
import br.com.alura.bytebank.domain.cliente.DadosCadastroCliente;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

public class ContaDAO {

    private Connection conn;

    ContaDAO(Connection connection){
        this.conn = connection;
    }
    public void salvar(DadosAberturaConta dadosDaConta){
        String sql = "INSERT into conta (numero,saldo,cliente_nome,cliente_cpf,cliente_email,esta_ativa)" +
                "VALUES (?,?,?,?,?,?)";

        var cliente = new Cliente(dadosDaConta.dadosCliente());
        var conta = new Conta(dadosDaConta.numero(), cliente, BigDecimal.ZERO, true);
        try{
            PreparedStatement preparedStatement = conn.prepareStatement(sql);

            preparedStatement.setInt(1,conta.getNumero());
            preparedStatement.setBigDecimal(2, BigDecimal.ZERO);
            preparedStatement.setString(3,dadosDaConta.dadosCliente().nome());
            preparedStatement.setString(4,dadosDaConta.dadosCliente().cpf());
            preparedStatement.setString(5,dadosDaConta.dadosCliente().email());
            preparedStatement.setBoolean(6,true);
            preparedStatement.execute();
            preparedStatement.close();
            conn.close();
        } catch (SQLException e){
            throw new RuntimeException(e);
        }
    }
    public Set<Conta> listar(){
        Set<Conta> contas = new HashSet<>();
        PreparedStatement ps;
        ResultSet resultSet;
        String sql = "SELECT * FROM conta WHERE esta_ativa = true";
        try{
            ps = conn.prepareStatement(sql);
            resultSet = ps.executeQuery();

            while(resultSet.next()){
                Integer numero = resultSet.getInt(1);
                BigDecimal saldo = resultSet.getBigDecimal(2);
                String nome = resultSet.getString(3);
                String cpf = resultSet.getString(4);
                String email = resultSet.getString(5);
                boolean estaAtiva = resultSet.getBoolean(6);
                DadosCadastroCliente dadosCadastroCliente = new DadosCadastroCliente(nome,cpf,email);
                Cliente cliente = new Cliente(dadosCadastroCliente);
                contas.add(new Conta(numero, cliente, saldo, estaAtiva));
            }
            resultSet.close();
            ps.close();
            conn.close();
        } catch (SQLException e){
            throw new RuntimeException(e);
        }
        return contas;

    }
    public BigDecimal RecuperaSaldo(Integer numeroDaConta) {
        PreparedStatement ps;
        BigDecimal saldo = null;
        ResultSet rs;
        String sql = "SELECT saldo FROM conta WHERE numero = ? AND esta_ativa = true";
        try{
            ps = conn.prepareStatement(sql);
            ps.setInt( 1,numeroDaConta);
            rs = ps.executeQuery();


            while(rs.next()) {
                saldo = rs.getBigDecimal(1);
            }
            rs.close();
            ps.close();
            conn.close();

        }catch(SQLException e){
            throw new RuntimeException(e);
        }
        return saldo;
    }
    public Conta buscarConta(Integer numeroconta){
        Conta conta = null;
        PreparedStatement ps;
        ResultSet resultSet;
        String sql = "SELECT * FROM conta WHERE numero = ?";
        try{
            ps = conn.prepareStatement(sql);
            ps.setInt(1,numeroconta);
            resultSet = ps.executeQuery();

            while(resultSet.next()){
                Integer numero = resultSet.getInt(1);
                BigDecimal saldo = resultSet.getBigDecimal(2);
                String nome = resultSet.getString(3);
                String cpf = resultSet.getString(4);
                String email = resultSet.getString(5);
                boolean estaAtiva = resultSet.getBoolean(6);
                DadosCadastroCliente dadosCadastroCliente = new DadosCadastroCliente(nome,cpf,email);
                Cliente cliente = new Cliente(dadosCadastroCliente);
                conta = new Conta(numero, cliente, saldo, estaAtiva);
            }
            resultSet.close();
            ps.close();
            conn.close();
        } catch (SQLException e){
            throw new RuntimeException(e);
        }
        return conta;
    }

    public void alterar(Integer numero, BigDecimal valor) {
        PreparedStatement ps;
        String sql = "UPDATE conta SET saldo = ? WHERE numero = ?";

        try {
            conn.setAutoCommit(false);

            ps = conn.prepareStatement(sql);

            ps.setBigDecimal(1, valor);
            ps.setInt(2, numero);

            ps.execute();
            conn.commit();
            ps.close();
            conn.close();
        } catch (SQLException e) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
            throw new RuntimeException(e);
        }
    }
    public void deleteConta(Integer numero){
        PreparedStatement ps;
        String sql = "DELETE from conta WHERE numero = ?";

        try{
            ps = conn.prepareStatement(sql);

            ps.setInt(1, numero);

            ps.execute();
            ps.close();
            conn.close();
        }catch(SQLException e){
            throw new RuntimeException(e);
        }
    }
    public void alterarLogico(Integer numero) {
        PreparedStatement ps;
        String sql = "UPDATE conta SET esta_ativa = false WHERE numero = ?";

        try {
            conn.setAutoCommit(false);

            ps = conn.prepareStatement(sql);

            ps.setInt(1, numero);

            ps.execute();
            conn.commit();
            ps.close();
            conn.close();
        } catch (SQLException e) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
            throw new RuntimeException(e);
        }
    }
}
