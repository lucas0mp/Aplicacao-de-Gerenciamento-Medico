// Aguarda o HTML carregar antes de executar
document.addEventListener("DOMContentLoaded", () => {
    
    // URL da sua API Backend (Spring Boot)
    const API_URL = "/api"; 

    // --- Elementos do HTML ---
    const loginContainer = document.getElementById("login-container");
    const appContainer = document.getElementById("app-container");
    const loginForm = document.getElementById("login-form");
    const loginError = document.getElementById("login-error");
    const logoutBtn = document.getElementById("logout-btn");
    
    const dataArea = document.getElementById("data-area");
    const userNameEl = document.getElementById("user-name");
    const userRoleEl = document.getElementById("user-role");

    // --- Sessão do Usuário ---
    let sessao = {}; // Guarda quem está logado

    // ===================================================================
    // 1. LÓGICA DE LOGIN
    // ===================================================================
    loginForm.addEventListener("submit", async (e) => {
        e.preventDefault(); 
        loginError.textContent = "";
        const loginInput = document.getElementById("login-usuario").value;
        const senhaInput = document.getElementById("login-senha").value;

        try {
            const response = await fetch(`${API_URL}/login`, {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ login: loginInput, senha: senhaInput })
            });
            const data = await response.json();
            if (data.sucesso) {
                sessao = {
                    logado: true,
                    nome: data.nome,
                    id_usuario: data.dadosUsuario.id_usuario,
                    id_grupo: data.dadosUsuario.id_grupo,
                    id_perfil: data.dadosUsuario.id_perfil_paciente || data.dadosUsuario.id_perfil_medico || data.dadosUsuario.id_perfil_admin,
                    role: data.dadosUsuario.nome_grupo
                };
                mostrarApp();
            } else {
                loginError.textContent = data.mensagem;
            }
        } catch (err) {
            console.error("Erro de login:", err);
            loginError.textContent = "Erro ao conectar na API. O Backend (Java) está rodando?";
        }
    });

    // ===================================================================
    // 2. LÓGICA DE LOGOUT
    // ===================================================================
    logoutBtn.addEventListener("click", () => {
        sessao = {}; 
        appContainer.classList.add("hidden");
        loginContainer.classList.remove("hidden");
        loginForm.reset(); 
        dataArea.innerHTML = "";
    });

    // ===================================================================
    // 3. RENDERIZAÇÃO PRINCIPAL
    // ===================================================================
    function mostrarApp() {
        loginContainer.classList.add("hidden");
        appContainer.classList.remove("hidden");
        userNameEl.textContent = sessao.nome;
        userRoleEl.textContent = sessao.role;
        carregarDadosPorGrupo();
    }

    function carregarDadosPorGrupo() {
        dataArea.innerHTML = ""; 
        if (sessao.id_grupo === 1) { // Administrador
            renderDashboardAdmin();
        } 
        else if (sessao.id_grupo === 2) { // Médico
            renderDashboardMedico();
        } 
        else if (sessao.id_grupo === 3) { // Paciente
            renderDashboardPaciente();
        }
    }

    // Função helper para exibir feedback
    function showFeedback(elementId, message, isSuccess) {
        const el = document.getElementById(elementId);
        if (el) {
            el.textContent = message;
            el.className = isSuccess ? "success-msg" : "error-msg";
        }
    }

    // ===================================================================
    // 4. DASHBOARD DO PACIENTE (Grupo 3) - (Sem Mudanças)
    // ===================================================================
    async function renderDashboardPaciente() {
        dataArea.innerHTML = `
            <div class="coluna-flex-2">
                <div class="card">
                    <h3>Meus Lembretes de Hoje</h3>
                    <p>REQUISITO: Lendo da <strong>View vw_lembretes_hoje</strong></p>
                    <table id="tabela-lembretes">
                        <thead><tr><th>Medicamento</th><th>Dosagem</th><th>Horário</th><th>Ação</th></tr></thead>
                        <tbody></tbody>
                    </table>
                </div>
                <div class="card">
                    <h3>Meu Histórico de Medições</h3>
                    <p>REQUISITO: Lendo dados das tabelas <strong>medicao</strong>, <strong>medicao_glicemia</strong>, etc.</p>
                    <div id="lista-medicoes"></div>
                </div>
            </div>
            <div class="coluna">
                <div class="card" id="nosql-area">
                    <h3>Diário do Paciente (NoSQL)</h3>
                    <p>REQUISITO: Salva dados flexíveis no <strong>MongoDB</strong>.</p>
                    <form id="diario-form">
                        <div class="input-group">
                            <label for="diario-titulo">Título (ex: "Medição em jejum")</label>
                            <input type="text" id="diario-titulo" required>
                        </div>
                        <div class="input-group">
                            <label for="diario-texto">Como você está se sentindo?</label>
                            <textarea id="diario-texto" rows="3" required></textarea>
                        </div>
                        <div class="input-group">
                            <label for="diario-sintomas">Sintomas (separados por vírgula)</label>
                            <input type="text" id="diario-sintomas" placeholder="ex: dor de cabeça, tontura">
                        </div>
                        <button type="submit" class="btn btn-primary">Salvar Diário (NoSQL)</button>
                        <p id="nosql-success" class="success-msg"></p>
                    </form>
                </div>
            </div>
        `;

        // 1. Buscar e preencher lembretes
        try {
            const response = await fetch(`${API_URL}/paciente/${sessao.id_perfil}/lembretes`);
            const lembretes = await response.json();
            const tbody = document.getElementById("tabela-lembretes").querySelector("tbody");
            if (lembretes.length === 0) {
                tbody.innerHTML = '<tr><td colspan="4">Nenhum lembrete para hoje.</td></tr>';
            } else {
                lembretes.forEach(l => {
                    tbody.innerHTML += `
                        <tr>
                            <td>${l.medicamento}</td>
                            <td>${l.dosagem}</td>
                            <td>${l.horario}</td>
                            <td><button class="btn btn-success btn-tomar" data-id="${l.id_lembrete}">Tomar</button></td>
                        </tr>
                    `;
                });
            }
            tbody.querySelectorAll('.btn-tomar').forEach(btn => {
                btn.addEventListener('click', async (e) => {
                    const idLembrete = e.target.dataset.id;
                    await fetch(`${API_URL}/paciente/lembrete/${idLembrete}/tomar`, { method: 'PUT' });
                    e.target.textContent = "Tomado!";
                    e.target.disabled = true;
                    e.target.classList.remove('btn-success');
                    e.target.classList.add('btn-secondary');
                });
            });
        } catch (e) { console.error("Erro ao buscar lembretes:", e); }

        // 2. Buscar e preencher histórico de medições
        try {
            const response = await fetch(`${API_URL}/paciente/${sessao.id_perfil}/medicoes`);
            const medicoes = await response.json();
            const lista = document.getElementById("lista-medicoes");
            if (medicoes.length === 0) {
                lista.innerHTML = "<p>Nenhum histórico de medição encontrado.</p>";
            } else {
                lista.innerHTML = medicoes.map(m => `<p style="font-size: 0.9rem; border-bottom: 1px solid #eee; padding-bottom: 5px;">${m}</p>`).join('');
            }
        } catch (e) { console.error("Erro ao buscar medições:", e); }

        // 3. Lógica do formulário NoSQL
        document.getElementById("diario-form").addEventListener("submit", async (e) => {
            e.preventDefault();
            const successMsg = document.getElementById("nosql-success");
            successMsg.textContent = "Salvando...";
            const diarioDoc = {
                idUsuarioSql: sessao.id_usuario, 
                titulo: document.getElementById("diario-titulo").value,
                textoLivre: document.getElementById("diario-texto").value,
                sintomas: document.getElementById("diario-sintomas").value.split(',').map(s => s.trim()).filter(s => s.length > 0)
            };
            try {
                const response = await fetch(`${API_URL}/paciente/diario`, {
                    method: "POST",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify(diarioDoc)
                });
                const data = await response.json();
                showFeedback("nosql-success", data.mensagem, data.sucesso);
                if (data.sucesso) e.target.reset();
            } catch (err) {
                showFeedback("nosql-success", "Erro ao conectar na API do Diário.", false);
            }
        });
    }

    // ===================================================================
    // 5. DASHBOARD DO MÉDICO (Grupo 2) - (MODIFICADO)
    // ===================================================================
    async function renderDashboardMedico() {
        dataArea.innerHTML = `
            <div class="coluna">
                <div class="card">
                    <h3>Meus Pacientes (CRUD)</h3>
                    <p>REQUISITO: Lendo da <strong>View vw_medico_paciente</strong></p>
                    <table id="tabela-meus-pacientes">
                        <thead><tr><th>ID</th><th>Nome</th><th>CPF</th><th>Email</th><th>Ações</th></tr></thead>
                        <tbody></tbody>
                    </table>
                </div>
                <div class="card">
                    <h3>Registrar Medição</h3>
                    <p>REQUISITO: Chamando <strong>Procedures</strong> (ex: sp_registrar_medicao_glicemia)</p>
                    <form id="form-medicao-glicemia">
                        <h4>Registrar Glicemia</h4>
                        <div class="input-group">
                            <label>ID do Paciente</label>
                            <input type="number" id="glic-paciente-id" required>
                        </div>
                        <div class="input-group">
                            <label>Nível (ex: 99.5)</label>
                            <input type="number" step="0.1" id="glic-nivel" required>
                        </div>
                        <div class="input-group">
                            <label>Período (ex: Jejum)</label>
                            <input type="text" id="glic-periodo" value="Jejum" required>
                        </div>
                        <button type="submit" class="btn btn-primary">Salvar Glicemia</button>
                        <p id="medicao-success" class="success-msg"></p>
                    </form>
                    </div>
            </div>
            <div class="coluna">
                <div class="card">
                    <h3>Criar Novo Paciente</h3>
                    <p>REQUISITO: Chama <strong>Procedure sp_registrar_novo_paciente</strong></p>
                    <form id="form-medico-paciente">
                        <div class="input-group"><label>Nome</label><input type="text" id="pac-nome" required></div>
                        <div class="input-group"><label>CPF</label><input type="text" id="pac-cpf" required></div>
                        <div class="input-group"><label>Email</label><input type="email" id="pac-email"></div>
                        <div class="input-group"><label>Data Nasc. (AAAA-MM-DD)</label><input type="text" id="pac-data" placeholder="yyyy-mm-dd" required></div>
                        <div class="input-group"><label>Celular</label><input type="text" id="pac-celular"></div>
                        <div class="input-group"><label>Senha Provisória</label><input type="password" id="pac-senha" required></div>
                        <button type="submit" class="btn btn-primary">Criar Paciente</button>
                        <p id="medico-paciente-success" class="success-msg"></p>
                    </form>
                </div>
            </div>
        `;

        // --- Lógica do Médico ---
        
        // 1. Buscar e preencher "Meus Pacientes"
        async function carregarPacientesMedico() {
            try {
                const response = await fetch(`${API_URL}/medico/${sessao.id_perfil}/pacientes`);
                const pacientes = await response.json();
                const tbody = document.getElementById("tabela-meus-pacientes").querySelector("tbody");
                tbody.innerHTML = "";
                if (pacientes.length === 0) {
                    tbody.innerHTML = '<tr><td colspan="5">Você ainda não acompanha nenhum paciente.</td></tr>';
                } else {
                    pacientes.forEach(p => {
                        // ** BOTÕES ADICIONADOS AQUI **
                        tbody.innerHTML += `
                            <tr data-id-pac="${p.id_paciente}" data-nome-pac="${p.nome_paciente}" data-email-pac="${p.email_paciente || ''}" data-celular-pac="${p.telefone_celular || ''}" data-nasc-pac="${p.data_nascimento || ''}">
                                <td>${p.id_paciente}</td>
                                <td>${p.nome_paciente}</td>
                                <td>${p.cpf_paciente}</td>
                                <td>${p.email_paciente}</td>
                                <td>
                                    <button class="btn btn-secondary btn-edit-paciente-med" data-id="${p.id_paciente}">Editar</button>
                                    <button class="btn btn-danger btn-delete-paciente-med" data-id="${p.id_paciente}">Excluir</button>
                                </td>
                            </tr>
                        `;
                    });
                }
                
                // NOVO: Evento de Excluir Paciente (Médico)
                tbody.querySelectorAll('.btn-delete-paciente-med').forEach(btn => {
                    btn.addEventListener('click', async (e) => {
                        const id = e.target.dataset.id;
                        if (confirm(`Tem certeza que quer excluir o PACIENTE ID ${id}?`)) {
                            await fetch(`${API_URL}/medico/pacientes/${id}`, { method: 'DELETE' });
                            document.querySelector(`tr[data-id-pac="${id}"]`).remove();
                        }
                    });
                });
                
                // NOVO: Evento de Editar Paciente (Médico)
                tbody.querySelectorAll('.btn-edit-paciente-med').forEach(btn => {
                    btn.addEventListener('click', async (e) => {
                        const id = e.target.dataset.id;
                        const tr = document.querySelector(`tr[data-id-pac="${id}"]`);
                        
                        // Pega dados atuais (dos atributos data- da linha da tabela)
                        const nomeAtual = tr.dataset.nomePac;
                        const emailAtual = tr.dataset.emailPac;
                        const celAtual = tr.dataset.celularPac;
                        const dataNascAtual = tr.dataset.nascPac.split('T')[0]; // Formata data AAAA-MM-DD
                        
                        // Pede novos dados
                        const novoNome = prompt("Novo Nome:", nomeAtual);
                        if (novoNome === null) return; // Cancelou
                        
                        const novoEmail = prompt("Novo Email:", emailAtual);
                        const novoCelular = prompt("Novo Celular:", celAtual);
                        const novaDataNasc = prompt("Nova Data Nasc. (AAAA-MM-DD):", dataNascAtual);

                        const pacienteAtualizado = {
                            nome: novoNome,
                            email: novoEmail,
                            telefone_celular: novoCelular,
                            data_nascimento: novaDataNasc,
                            // (CPF não pode ser editado)
                        };

                        await fetch(`${API_URL}/medico/pacientes/${id}`, { 
                            method: 'PUT',
                            headers: { "Content-Type": "application/json" },
                            body: JSON.stringify(pacienteAtualizado)
                        });
                        
                        // Recarrega a lista para ver a mudança
                        carregarPacientesMedico();
                    });
                });
                
            } catch (e) { console.error("Erro ao buscar pacientes:", e); }
        }

        // 2. Lógica do formulário de Glicemia
        document.getElementById("form-medicao-glicemia").addEventListener("submit", async (e) => {
            e.preventDefault();
            const dados = {
                idPaciente: document.getElementById("glic-paciente-id").value,
                nivel: document.getElementById("glic-nivel").value,
                periodo: document.getElementById("glic-periodo").value,
                obs: "Registrado pelo(a) " + sessao.nome
            };
            try {
                const response = await fetch(`${API_URL}/medico/medicao/glicemia`, {
                    method: "POST",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify(dados)
                });
                const data = await response.json();
                showFeedback("medicao-success", data.mensagem, data.sucesso);
                if (data.sucesso) e.target.reset();
            } catch (e) {
                showFeedback("medicao-success", "Erro ao salvar medição.", false);
            }
        });

        // 3. Lógica do formulário de Criar Paciente (Médico)
        document.getElementById("form-medico-paciente").addEventListener("submit", async (e) => {
            e.preventDefault();
            const feedbackEl = "medico-paciente-success";
            showFeedback(feedbackEl, "Salvando...", true);
            const dados = {
                nome: document.getElementById("pac-nome").value,
                cpf: document.getElementById("pac-cpf").value,
                email: document.getElementById("pac-email").value,
                data_nascimento: document.getElementById("pac-data").value,
                telefone_celular: document.getElementById("pac-celular").value,
                senha: document.getElementById("pac-senha").value,
                id_medico_responsavel: sessao.id_perfil // O ID do médico logado
            };
            try {
                const response = await fetch(`${API_URL}/medico/pacientes`, { 
                    method: "POST",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify(dados)
                });
                const data = await response.json();
                showFeedback(feedbackEl, data.mensagem, data.sucesso);
                if (data.sucesso) {
                    e.target.reset();
                    carregarPacientesMedico(); // Atualiza a lista
                }
            } catch (e) {
                showFeedback(feedbackEl, "Erro fatal ao criar paciente.", false);
            }
        });

        // Carga inicial
        carregarPacientesMedico();
    }

    // ===================================================================
    // 6. DASHBOARD DO ADMIN (Grupo 1) - (MODIFICADO)
    // ===================================================================
    async function renderDashboardAdmin() {
        dataArea.innerHTML = `
            <div class="coluna-flex-2">
                <div class="card">
                    <h3>Gerenciar Pacientes (CRUD)</h3>
                    <p>REQUISITO: Lendo da <strong>View vw_medico_paciente</strong></p>
                    <table id="tabela-admin-pacientes">
                        <thead><tr><th>ID</th><th>Paciente</th><th>CPF</th><th>Médico Responsável</th><th>Ações</th></tr></thead>
                        <tbody></tbody>
                    </table>
                </div>
                 <div class="card">
                    <h3>Gerenciar Médicos (CRUD)</h3>
                    <p>REQUISITO: Lendo da tabela <strong>medico</strong> e usando <strong>Função 'proximo_id'</strong></p>
                    <table id="tabela-admin-medicos">
                        <thead><tr><th>ID</th><th>Nome</th><th>CRM</th><th>Especialidade</th><th>Ações</th></tr></thead>
                        <tbody></tbody>
                    </table>
                </div>
            </div>
            <div class="coluna">
                <div class="card">
                    <h3>Criar Novo Paciente</h3>
                    <p>REQUISITO: Chamando <strong>Procedure sp_registrar_novo_paciente</strong></p>
                    <form id="form-novo-paciente">
                        <div class="input-group"><label>Nome</label><input type="text" id="pac-nome" required></div>
                        <div class="input-group"><label>CPF</label><input type="text" id="pac-cpf" required></div>
                        <div class="input-group"><label>Email</label><input type="email" id="pac-email"></div>
                        <div class="input-group"><label>Data Nasc. (AAAA-MM-DD)</label><input type="text" id="pac-data" placeholder="yyyy-mm-dd" required></div>
                        <div class="input-group"><label>Celular</label><input type="text" id="pac-celular"></div>
                        <div class="input-group"><label>Senha Provisória</label><input type="password" id="pac-senha" required></div>
                        <div class="input-group"><label>ID do Médico Responsável</label><input type="number" id="pac-id-medico" required></div>
                        <button type="submit" class="btn btn-primary">Criar Paciente</button>
                        <p id="admin-pac-success" class="success-msg"></p>
                    </form>
                </div>
                <div class="card">
                    <h3>Criar Novo Médico</h3>
                    <p>REQUISITO: Chama <strong>Função proximo_id</strong></p>
                    <form id="form-novo-medico">
                        <div class="input-group"><label>Nome</label><input type="text" id="med-nome" required></div>
                        <div class="input-group"><label>CRM</label><input type="text" id="med-crm" required></div>
                        <div class="input-group"><label>Especialidade</label><input type="text" id="med-esp" required></div>
                        <div class="input-group"><label>Senha Provisória</label><input type="password" id="med-senha" required></div>
                        <button type="submit" class="btn btn-primary">Criar Médico</button>
                        <p id="admin-med-success" class="success-msg"></p>
                    </form>
                </div>
            </div>
        `;
        
        // --- Lógica do Admin (CRUD Completo) ---
        
        const adminPacSuccess = document.getElementById("admin-pac-success");
        const adminMedSuccess = document.getElementById("admin-med-success");

        // 1. Função para carregar Pacientes
        async function carregarPacientesAdmin() {
            try {
                const response = await fetch(`${API_URL}/admin/pacientes`);
                const pacientes = await response.json();
                const tbody = document.getElementById("tabela-admin-pacientes").querySelector("tbody");
                tbody.innerHTML = "";
                pacientes.forEach(p => {
                    // ** BOTÕES ADICIONADOS AQUI **
                    tbody.innerHTML += `
                        <tr data-id-pac="${p.id_paciente}" data-nome-pac="${p.nome_paciente}" data-email-pac="${p.email_paciente || ''}" data-celular-pac="${p.telefone_celular || ''}" data-nasc-pac="${p.data_nascimento || ''}">
                            <td>${p.id_paciente}</td>
                            <td>${p.nome_paciente}</td>
                            <td>${p.cpf_paciente}</td>
                            <td>${p.nome_medico} (ID: ${p.id_medico})</td>
                            <td>
                                <button class="btn btn-secondary btn-edit-paciente" data-id="${p.id_paciente}">Editar</button>
                                <button class="btn btn-danger btn-delete-paciente" data-id="${p.id_paciente}">Excluir</button>
                            </td>
                        </tr>
                    `;
                });
                
                // Evento de Excluir Paciente
                tbody.querySelectorAll('.btn-delete-paciente').forEach(btn => {
                    btn.addEventListener('click', async (e) => {
                        const id = e.target.dataset.id;
                        if (confirm(`Tem certeza que quer excluir o PACIENTE ID ${id}?`)) {
                            await fetch(`${API_URL}/admin/pacientes/${id}`, { method: 'DELETE' });
                            document.querySelector(`tr[data-id-pac="${id}"]`).remove();
                        }
                    });
                });
                
                // NOVO: Evento de Editar Paciente (Admin)
                tbody.querySelectorAll('.btn-edit-paciente').forEach(btn => {
                    btn.addEventListener('click', async (e) => {
                        const id = e.target.dataset.id;
                        const tr = document.querySelector(`tr[data-id-pac="${id}"]`);
                        
                        const nomeAtual = tr.dataset.nomePac;
                        const emailAtual = tr.dataset.emailPac;
                        const celAtual = tr.dataset.celularPac;
                        const dataNascAtual = tr.dataset.nascPac.split('T')[0];
                        
                        const novoNome = prompt("Novo Nome:", nomeAtual);
                        if (novoNome === null) return;
                        
                        const novoEmail = prompt("Novo Email:", emailAtual);
                        const novoCelular = prompt("Novo Celular:", celAtual);
                        const novaDataNasc = prompt("Nova Data Nasc. (AAAA-MM-DD):", dataNascAtual);

                        const pacienteAtualizado = {
                            nome: novoNome,
                            email: novoEmail,
                            telefone_celular: novoCelular,
                            data_nascimento: novaDataNasc
                        };

                        await fetch(`${API_URL}/admin/pacientes/${id}`, { 
                            method: 'PUT',
                            headers: { "Content-Type": "application/json" },
                            body: JSON.stringify(pacienteAtualizado)
                        });
                        
                        carregarPacientesAdmin(); // Recarrega
                    });
                });
                
            } catch (e) { console.error("Erro ao buscar pacientes admin:", e); }
        }

        // 2. Função para carregar Médicos
        async function carregarMedicosAdmin() {
            try {
                const response = await fetch(`${API_URL}/admin/medicos`);
                const medicos = await response.json();
                const tbody = document.getElementById("tabela-admin-medicos").querySelector("tbody");
                tbody.innerHTML = "";
                medicos.forEach(m => {
                    // ** BOTÕES ADICIONADOS AQUI **
                    tbody.innerHTML += `
                        <tr data-id-med="${m.id_medico}" data-nome-med="${m.nome}" data-crm-med="${m.crm}" data-esp-med="${m.especialidade}">
                            <td>${m.id_medico}</td>
                            <td>${m.nome}</td>
                            <td>${m.crm}</td>
                            <td>${m.especialidade}</td>
                            <td>
                                <button class="btn btn-secondary btn-edit-medico" data-id="${m.id_medico}">Editar</button>
                                <button class="btn btn-danger btn-delete-medico" data-id="${m.id_medico}">Excluir</button>
                            </td>
                        </tr>
                    `;
                });

                // Evento de Excluir Médico
                tbody.querySelectorAll('.btn-delete-medico').forEach(btn => {
                    btn.addEventListener('click', async (e) => {
                        const id = e.target.dataset.id;
                        if (confirm(`Tem certeza que quer excluir o MÉDICO ID ${id}?`)) {
                            await fetch(`${API_URL}/admin/medicos/${id}`, { method: 'DELETE' });
                            document.querySelector(`tr[data-id-med="${id}"]`).remove();
                        }
                    });
                });
                
                // NOVO: Evento de Editar Médico (Admin)
                tbody.querySelectorAll('.btn-edit-medico').forEach(btn => {
                    btn.addEventListener('click', async (e) => {
                        const id = e.target.dataset.id;
                        const tr = document.querySelector(`tr[data-id-med="${id}"]`);

                        const nomeAtual = tr.dataset.nomeMed;
                        const crmAtual = tr.dataset.crmMed;
                        const espAtual = tr.dataset.espMed;
                        
                        const novoNome = prompt("Novo Nome:", nomeAtual);
                        if (novoNome === null) return;
                        
                        const novoCrm = prompt("Novo CRM:", crmAtual);
                        const novaEsp = prompt("Nova Especialidade:", espAtual);

                        const medicoAtualizado = {
                            nome: novoNome,
                            crm: novoCrm,
                            especialidade: novaEsp
                        };

                        await fetch(`${API_URL}/admin/medicos/${id}`, { 
                            method: 'PUT',
                            headers: { "Content-Type": "application/json" },
                            body: JSON.stringify(medicoAtualizado)
                        });
                        
                        carregarMedicosAdmin(); // Recarrega
                    });
                });
                
            } catch (e) { console.error("Erro ao buscar medicos admin:", e); }
        }

        // 3. Lógica do formulário de Novo Paciente (Admin)
        document.getElementById("form-novo-paciente").addEventListener("submit", async (e) => {
            e.preventDefault();
            showFeedback("admin-pac-success", "Salvando...", true);
            const dados = {
                nome: document.getElementById("pac-nome").value,
                cpf: document.getElementById("pac-cpf").value,
                email: document.getElementById("pac-email").value,
                data_nascimento: document.getElementById("pac-data").value,
                telefone_celular: document.getElementById("pac-celular").value,
                senha: document.getElementById("pac-senha").value,
                id_medico_responsavel: document.getElementById("pac-id-medico").value,
            };
            try {
                const response = await fetch(`${API_URL}/admin/pacientes`, {
                    method: "POST",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify(dados)
                });
                const data = await response.json();
                showFeedback("admin-pac-success", data.mensagem, data.sucesso);
                if (data.sucesso) {
                    e.target.reset();
                    carregarPacientesAdmin(); 
                }
            } catch (e) {
                showFeedback("admin-pac-success", "Erro fatal ao salvar paciente.", false);
            }
        });
        
        // 4. Lógica do formulário de Novo Médico (Admin)
        document.getElementById("form-novo-medico").addEventListener("submit", async (e) => {
            e.preventDefault();
            showFeedback("admin-med-success", "Salvando...", true);
            const dados = {
                nome: document.getElementById("med-nome").value,
                crm: document.getElementById("med-crm").value,
                especialidade: document.getElementById("med-esp").value,
                senha: document.getElementById("med-senha").value
            };
            try {
                const response = await fetch(`${API_URL}/admin/medicos`, {
                    method: "POST",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify(dados)
                });
                const data = await response.json();
                showFeedback("admin-med-success", data.mensagem, data.sucesso);
                if (data.sucesso) {
                    e.target.reset();
                    carregarMedicosAdmin();
                }
            } catch (e) {
                showFeedback("admin-med-success", "Erro fatal ao salvar médico.", false);
            }
        });
        
        // --- Carga Inicial do Admin ---
        carregarPacientesAdmin();
        carregarMedicosAdmin();
    }
});