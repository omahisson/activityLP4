import { useEffect, useState } from 'react';
import { createRoot } from 'react-dom/client';
import './styles.css';

const endpoint = '/api/v1/responsaveis';
const avisoConexao = 'Não foi possível conectar à API. Tente novamente.';

function CadastroResponsaveis() {
  const [resultado, setResultado] = useState(null);
  const [enviando, setEnviando] = useState(false);
  const [revisaoListagem, setRevisaoListagem] = useState(0);
  const [listagem, setListagem] = useState({ dados: [], status: 'carregando' });

  useEffect(() => {
    const controlador = new AbortController();
    const { signal } = controlador;

    async function carregarResponsaveis() {
      setListagem((anterior) => ({ ...anterior, status: 'carregando' }));

      try {
        const resposta = await fetch(endpoint, { signal, cache: 'no-store' });
        if (!resposta.ok) {
          throw new Error('Falha ao consultar responsáveis');
        }

        const dados = await resposta.json();
        if (!signal.aborted) {
          setListagem({ dados, status: 'pronta' });
        }
      } catch {
        if (!signal.aborted) {
          setListagem((anterior) => ({ ...anterior, status: 'erro' }));
        }
      }
    }

    carregarResponsaveis();
    return () => controlador.abort();
  }, [revisaoListagem]);

  function atualizarListagem() {
    setRevisaoListagem((anterior) => anterior + 1);
  }

  async function cadastrar(evento) {
    evento.preventDefault();

    const formulario = evento.currentTarget;
    const dados = Object.fromEntries(new FormData(formulario));
    // O backend exige a repetição. A tela coleta a senha uma única vez.
    dados.senhaRepeticao = dados.senha;

    setResultado(null);
    setEnviando(true);

    try {
      const resposta = await fetch(endpoint, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(dados),
      });

      if (resposta.status === 201) {
        setResultado({ tipo: 'sucesso', texto: 'Responsável cadastrado com sucesso' });
        formulario.reset();
        atualizarListagem();
      } else if (resposta.status === 400) {
        setResultado({ tipo: 'erro', texto: await resposta.text() });
      } else {
        setResultado({ tipo: 'erro', texto: avisoConexao });
      }
    } catch {
      setResultado({ tipo: 'erro', texto: avisoConexao });
    } finally {
      setEnviando(false);
    }
  }

  return (
    <>
      <header className="cabecalho">
        <div className="largura cabecalho-conteudo">Central Nuclear Vladimir Ilyich Lenin</div>
      </header>

      <main className="largura">
        <section className="painel" aria-labelledby="titulo-cadastro">
          <h1 id="titulo-cadastro">Cadastro de responsáveis</h1>
          <form id="form-cadastro" noValidate onSubmit={cadastrar}>
            <div className="campos">
              <div className="campo">
                <label htmlFor="nome">Nome</label>
                <input id="nome" name="nome" autoComplete="name" />
              </div>
              <div className="campo">
                <label htmlFor="cpf">CPF</label>
                <input id="cpf" name="cpf" inputMode="numeric" />
              </div>
              <div className="campo">
                <label htmlFor="email">E-mail</label>
                <input id="email" name="email" type="email" autoComplete="email" />
              </div>
              <div className="campo">
                <label htmlFor="celular">Celular</label>
                <input id="celular" name="celular" type="tel" autoComplete="tel" />
              </div>
              <div className="campo">
                <label htmlFor="login">Login</label>
                <input id="login" name="login" autoComplete="username" />
              </div>
              <div className="campo">
                <label htmlFor="senha">Senha</label>
                <input id="senha" name="senha" type="password" autoComplete="new-password" />
              </div>
            </div>
            <button id="cadastrar-responsavel" type="submit" disabled={enviando}>
              Cadastrar responsável
            </button>
            <p id="mensagem-resultado" className="resultado" data-tipo={resultado?.tipo} role="status">
              {resultado?.texto}
            </p>
          </form>
        </section>

        <section className="painel" aria-labelledby="titulo-listagem">
          <h2 id="titulo-listagem">Responsáveis cadastrados</h2>
          {listagem.status === 'erro' && (
            <div className="aviso-listagem">
              <p id="aviso-listagem" role="status">Não foi possível conectar à API para carregar a lista.</p>
              <button type="button" onClick={atualizarListagem}>
                Tentar novamente
              </button>
            </div>
          )}
          <table id="tabela-responsaveis" aria-labelledby="titulo-listagem" aria-busy={listagem.status === 'carregando'}>
            <thead>
              <tr>
                <th scope="col">Nome</th>
                <th scope="col">Login</th>
              </tr>
            </thead>
            <tbody>
              {listagem.dados.map(({ id, nome, login }) => (
                <tr key={id}>
                  <td>{nome}</td>
                  <td>{login}</td>
                </tr>
              ))}
              {listagem.dados.length === 0 && listagem.status === 'carregando' && (
                <tr><td colSpan={2} className="estado-listagem">Carregando responsáveis…</td></tr>
              )}
              {listagem.dados.length === 0 && listagem.status === 'pronta' && (
                <tr><td colSpan={2} className="estado-listagem">Nenhum responsável cadastrado.</td></tr>
              )}
            </tbody>
          </table>
        </section>
      </main>
    </>
  );
}

createRoot(document.getElementById('root')).render(<CadastroResponsaveis />);
