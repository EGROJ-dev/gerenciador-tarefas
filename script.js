// Versão web do Gerenciador de Tarefas.
// As regras são as mesmas do projeto em Java: título obrigatório,
// prazo não pode estar no passado e a lista mostra pendentes primeiro,
// da maior para a menor prioridade.

const CHAVE = "gerenciador-tarefas:v1";
const PESO = { baixa: 1, media: 2, alta: 3 };
const ROTULO = { baixa: "Baixa", media: "Média", alta: "Alta" };

let tarefas = carregar();
let filtro = "todas";

const form = document.getElementById("form");
const campoTitulo = document.getElementById("titulo");
const campoPrioridade = document.getElementById("prioridade");
const campoPrazo = document.getElementById("prazo");
const erro = document.getElementById("erro");
const lista = document.getElementById("lista");
const vazio = document.getElementById("vazio");
const resumo = document.getElementById("resumo");

/* ---------- armazenamento (fica só no seu navegador) ---------- */
function carregar() {
  try {
    return JSON.parse(localStorage.getItem(CHAVE)) || [];
  } catch (e) {
    return [];
  }
}

function salvar() {
  try {
    localStorage.setItem(CHAVE, JSON.stringify(tarefas));
  } catch (e) {
    /* sem armazenamento disponível: a lista funciona só nesta sessão */
  }
}

/* ---------- datas ---------- */
function hoje() {
  const d = new Date();
  const mes = String(d.getMonth() + 1).padStart(2, "0");
  const dia = String(d.getDate()).padStart(2, "0");
  return `${d.getFullYear()}-${mes}-${dia}`;
}

function formatar(iso) {
  const [a, m, d] = iso.split("-");
  return `${d}/${m}/${a}`;
}

function estaAtrasada(t) {
  return !t.concluida && t.prazo && t.prazo < hoje();
}

/* ---------- regras ---------- */
function proximoId() {
  return tarefas.reduce((max, t) => Math.max(max, t.id), 0) + 1;
}

function adicionar(titulo, prioridade, prazo) {
  if (!titulo.trim()) throw new Error("Digite o título da tarefa.");
  if (prazo && prazo < hoje()) throw new Error("O prazo não pode estar no passado.");
  tarefas.push({ id: proximoId(), titulo: titulo.trim(), prioridade, prazo, concluida: false });
  salvar();
}

function ordenadas() {
  return [...tarefas].sort((a, b) =>
    (a.concluida - b.concluida) || (PESO[b.prioridade] - PESO[a.prioridade]) || (a.id - b.id)
  );
}

function filtradas() {
  const todas = ordenadas();
  if (filtro === "pendentes") return todas.filter(t => !t.concluida);
  if (filtro === "atrasadas") return todas.filter(estaAtrasada);
  return todas;
}

/* ---------- tela ---------- */
function desenhar() {
  const itens = filtradas();
  lista.replaceChildren();

  for (const t of itens) {
    const li = document.createElement("li");
    li.className = t.prioridade + (t.concluida ? " feita" : "");

    const check = document.createElement("input");
    check.type = "checkbox";
    check.checked = t.concluida;
    check.setAttribute("aria-label", `Concluir: ${t.titulo}`);
    check.addEventListener("change", () => {
      t.concluida = check.checked;
      salvar();
      desenhar();
    });

    const texto = document.createElement("div");
    const titulo = document.createElement("p");
    titulo.className = "titulo";
    titulo.textContent = t.titulo;

    const meta = document.createElement("p");
    meta.className = "meta";
    let info = `Prioridade ${ROTULO[t.prioridade].toLowerCase()}`;
    info += t.prazo ? `, prazo ${formatar(t.prazo)}` : ", sem prazo";
    meta.textContent = info;
    if (estaAtrasada(t)) {
      const aviso = document.createElement("span");
      aviso.className = "atraso";
      aviso.textContent = " (atrasada)";
      meta.append(aviso);
    }
    texto.append(titulo, meta);

    const botao = document.createElement("button");
    botao.type = "button";
    botao.className = "remover";
    botao.textContent = "Remover";
    botao.setAttribute("aria-label", `Remover: ${t.titulo}`);
    botao.addEventListener("click", () => {
      tarefas = tarefas.filter(x => x.id !== t.id);
      salvar();
      desenhar();
    });

    li.append(check, texto, botao);
    lista.append(li);
  }

  if (itens.length === 0) {
    const mensagens = {
      todas: "Nenhuma tarefa ainda. Adicione a primeira acima.",
      pendentes: "Nada pendente. Bom trabalho!",
      atrasadas: "Nenhuma tarefa atrasada."
    };
    vazio.textContent = mensagens[filtro];
    vazio.hidden = false;
  } else {
    vazio.hidden = true;
  }

  const pendentes = tarefas.filter(t => !t.concluida).length;
  const atrasadas = tarefas.filter(estaAtrasada).length;
  resumo.textContent = tarefas.length === 0
    ? "Sua lista está vazia."
    : `${pendentes} ${pendentes === 1 ? "pendente" : "pendentes"}, ${atrasadas} ${atrasadas === 1 ? "atrasada" : "atrasadas"}.`;
}

/* ---------- eventos ---------- */
form.addEventListener("submit", (e) => {
  e.preventDefault();
  try {
    adicionar(campoTitulo.value, campoPrioridade.value, campoPrazo.value);
    erro.textContent = "";
    form.reset();
    campoPrioridade.value = "media";
    campoTitulo.focus();
    desenhar();
  } catch (err) {
    erro.textContent = err.message;
  }
});

document.querySelectorAll(".filtros button").forEach(btn => {
  btn.addEventListener("click", () => {
    filtro = btn.dataset.filtro;
    document.querySelectorAll(".filtros button").forEach(b =>
      b.setAttribute("aria-pressed", String(b === btn))
    );
    desenhar();
  });
});

campoPrazo.min = hoje();
desenhar();
