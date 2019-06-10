<template>
    <div class="login-form">
        <h2 class="text-header">Вход в приложение</h2>
        <form method="post" action="/login">
            <div class="error-alert" v-if="this.error">
                Некорректные данные
            </div>
            <md-field>
                <label>Логин</label>
                <md-input id="username" name="username" type="text" v-model="user.username" required></md-input>
                <span class="md-error">Логин отсутствует</span>
            </md-field>
            <md-field>
                <label>Пароль</label>
                <md-input id="password" name="password" type="password" v-model="user.password" required></md-input>
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
    .office-page{
        background-image: url("background.jpg");
        background-size: cover;
    }

    .text-header{
        text-align: center;
    }

    .login-form {
        padding: 25px;
        box-shadow: 20px 15px 10px 5px rgba(0, 0, 0, .2);
        border: 1px solid #f3f3f3;
        border-radius: 25px;
        background-color: #f3f3f3;
        display: flex;
        font-family: Arial;
        flex-direction: column;
        width: 400px;
        justify-content: space-between;
        position: fixed;
        top: 50%;
        left: 50%;
        /* bring your own prefixes */
        transform: translate(-65%, -75%);
    }

    .error-alert{
        padding: 25px;
        border: 1px solid #b1000e;
        border-radius: 25px;
        display: flex;
        justify-content: center;
        align-items: center;
        background-color: #f0a189;
        color: #b1000e;
    }
</style>