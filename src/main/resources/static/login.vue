<template>
    <div class="login-form">
        <h2>Вход в приложение</h2>
        <form method="post" action="/login">
            <div class="error-alert" v-if="this.error">
                Некорректные данные
            </div>
            <md-field>
                <label>Логин:</label>
                <md-input  id="username" name="username" type="text" v-model="user.username" required></md-input>
                <span class="md-helper-text">Требуется ввести логин</span>
                <span class="md-error">Логин отсутствует</span>
            </md-field>
            <md-field>
                <label>Пароль:</label>
                <md-input  id="password" name="password" type="password" v-model="user.password" required></md-input>
                <span class="md-helper-text">Требуется ввести пароль</span>
                <span class="md-error">Пароль отсутствует</span>
            </md-field>
            <div>
                <md-button class="md-raised reg-btn" type="submit">Вход</md-button>
                <md-button class="md-raised reg-btn" v-on:click="register">Регистрация</md-button>
            </div>
        </form>
    </div>
</template>

<script>
    export default {
        name: 'login',
        data() {
            return {
                user: {
                    username: '',
                    password: ''
                },
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded'
                },
                error: false
            }
        },
        created() {
            let uri = window.location.search.substring(1);
            let params = new URLSearchParams(uri);
            this.error = params.get("error") === "";
        },
        methods: {
            login: function () {
                this.$http.post('/login', this.user, {headers: this.headers}).then((res) => {
                    console.log(res);
                    //window.location.href = '/';
                })
            },
            register: function () {
                window.location.href = '/register';
            }
            
        }
    }
</script>

<style>
    .login-form {
        display: flex;
        font-family: Arial;
        flex-direction: column;
        width: 350px;
        justify-content: space-between;
        position: fixed;
        top: 50%;
        left: 50%;
        /* bring your own prefixes */
        transform: translate(-65%, -75%);
    }

    .error-alert{
        padding: 25px;
        border: 2px solid #a10010;
        border-radius: 25px;
        display: flex;
        justify-content: center;
        align-items: center;
        background-color: #eb7868;
        color: #a10010;
    }
</style>