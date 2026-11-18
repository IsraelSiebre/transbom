# Acesso à aplicação

Todas as páginas e operações exigem autenticação. Para criar o primeiro usuário em um banco vazio, defina as variáveis de ambiente `APP_BOOTSTRAP_USERNAME` e `APP_BOOTSTRAP_PASSWORD` antes de iniciar a aplicação. Com Docker Compose, elas podem ser definidas no arquivo `.env` local.

O nome de usuário deve ter de 3 a 50 caracteres, usando letras sem acento, números, ponto, sublinhado ou hífen. A senha deve ter pelo menos 8 caracteres e no máximo 72 bytes em UTF-8. O primeiro usuário só é criado quando ainda não há usuários cadastrados. Após a criação, remova as variáveis de bootstrap do ambiente e reinicie a aplicação.

Acesse `/login` com essas credenciais. No painel de mão de obra, a opção **Criar usuário** permite a pessoas autenticadas cadastrar outros usuários; todos os usuários cadastrados têm acesso à aplicação e podem criar outros usuários. Use **Sair** para encerrar a sessão.
