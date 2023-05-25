package br.com.alura.bytebank.domain.conta;

import br.com.alura.bytebank.ConnectionFactory;
import br.com.alura.bytebank.domain.RegraDeNegocioException;
import br.com.alura.bytebank.domain.cliente.Cliente;

import java.sql.Connection;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

public class ContaService {
    private ConnectionFactory connection;
    public ContaService(){
        this.connection = new ConnectionFactory();
    }

    private Set<Conta> contas = new HashSet<>();

    public Set<Conta> listarContasAbertas() {
        Connection conn = connection.RecuperaConexao();
        return new ContaDAO(conn).listar();
    }

    public BigDecimal consultarSaldo(Integer numeroDaConta) {
        Connection conn = connection.RecuperaConexao();

        return new ContaDAO(conn).RecuperaSaldo(numeroDaConta);
    }

    public void abrir(DadosAberturaConta dadosDaConta) {

        Connection conn = connection.RecuperaConexao();
        new ContaDAO(conn).salvar(dadosDaConta);
    }

    public void realizarSaque(Integer numeroDaConta, BigDecimal valor) {
        Connection conn1 = connection.RecuperaConexao();
        var conta = new ContaDAO(conn1).buscarConta(numeroDaConta);
        if (valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RegraDeNegocioException("Valor do saque deve ser superior a zero!");
        }

        if (valor.compareTo(conta.getSaldo()) > 0) {
            throw new RegraDeNegocioException("Saldo insuficiente!");
        }
        if(!conta.getEstaAtiva()){
            throw new RegraDeNegocioException("Conta não está ativa");
        }
        Connection conn = connection.RecuperaConexao();
        BigDecimal novoValor = conta.getSaldo().subtract(valor);
        new ContaDAO(conn).alterar(conta.getNumero(), novoValor);
    }

    public void realizarDeposito(Integer numeroDaConta, BigDecimal valor) {
        Connection conn1 = connection.RecuperaConexao();
        var conta = new ContaDAO(conn1).buscarConta(numeroDaConta);

        if (valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RegraDeNegocioException("Valor do deposito deve ser superior a zero!");
        }
        if(!conta.getEstaAtiva()){
            throw new RegraDeNegocioException("Conta não está ativa");
        }
        Connection conn = connection.RecuperaConexao();
        BigDecimal novoValor = conta.getSaldo().add(valor);
        new ContaDAO(conn).alterar(conta.getNumero(), novoValor);
    }
    public void realizarTransferencia(Integer numeroContaOrigem, Integer numeroContaDestino, BigDecimal valor){
        this.realizarSaque(numeroContaOrigem, valor);
        this.realizarDeposito(numeroContaDestino, valor);
    }
    public void encerrarLogico(Integer numeroConta){
        Connection conn1 = connection.RecuperaConexao();
        var conta = new ContaDAO(conn1).buscarConta(numeroConta);
        if (conta.possuiSaldo()) {
            throw new RegraDeNegocioException("Conta não pode ser encerrada pois ainda possui saldo!");
        }

        Connection conn = connection.RecuperaConexao();

        new ContaDAO(conn).alterarLogico(conta.getNumero());
    }

    public void encerrar(Integer numeroDaConta) {
        Connection conn1 = connection.RecuperaConexao();
        var conta = new ContaDAO(conn1).buscarConta(numeroDaConta);
        if (conta.possuiSaldo()) {
            throw new RegraDeNegocioException("Conta não pode ser encerrada pois ainda possui saldo!");
        }

        Connection conn = connection.RecuperaConexao();

        new ContaDAO(conn).deleteConta(conta.getNumero());
    }

    private Conta buscarContaPorNumero(Integer numero) {
        return contas
                .stream()
                .filter(c -> c.getNumero() == numero)
                .findFirst()
                .orElseThrow(() -> new RegraDeNegocioException("Não existe conta cadastrada com esse número!"));
    }
}
