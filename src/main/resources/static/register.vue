<template>
    <div class="reg-form">
        <h2>Регистрация</h2>
        <div>
            <md-field>
                <label>Логин:</label>
                <md-input type="text" v-model="user.username" required></md-input>
                <span class="md-helper-text">Требуется ввести логин</span>
                <span class="md-error">Логин отсутствует</span>
            </md-field>
            <md-field>
                <label>Пароль:</label>
                <md-input type="password" v-model="user.password" required></md-input>
                <span class="md-helper-text">Требуется ввести пароль</span>
                <span class="md-error">Пароль отсутствует</span>
            </md-field>
            <md-field>
                <label>Подтверждение пароля:</label>
                <md-input type="password" v-model="user.passwordConfirmation" required></md-input>
                <span class="md-helper-text">Введите пароль еще раз</span>
                <span class="md-error">Пароль отсутствует</span>
            </md-field>
            <md-field>
                <label>Имя:</label>
                <md-input type="text" v-model="user.firstName" required></md-input>
                <span class="md-helper-text">Необходимо ввести имя</span>
                <span class="md-error">Имя отсутствует</span>
            </md-field>
            <md-field>
                <label>Фамилия:</label>
                <md-input type="text" v-model="user.lastName" required></md-input>
                <span class="md-helper-text">Необходимо ввести фамилию</span>
                <span class="md-error">Фамилия отсутствует</span>
            </md-field>
            <md-field>
                <label>Отчество:</label>
                <md-input type="text" v-model="user.middleName" required></md-input>
                <span class="md-helper-text">Необходимо ввести отчество</span>
                <span class="md-error">Отчество отсутствует</span>
            </md-field>
        </div>
        <div class="form-btns">
            <span class="error-label" v-if="passwordConfirmError">Ошибка: пароли не совпадают</span>
            <span class="error-label" v-if="error">Обязательные поля не заполнены</span>
            <md-button class="md-raised reg-btn" v-on:click="register">Регистрация</md-button>
        </div>
    </div>

</template>

<script>
    export default {
        name: 'register',
        data() {
            return {
                user: {
                    username: "",
                    password: "",
                    firstName: '',
                    lastName: '',
                    middleName: '',
                },
                passwordConfirmation: '',
                passwordConfirmError: false,
                error: false

            }
        },
        methods: {
            register: function () {
                if (!this.user.password || this.user.password !== this.passwordConfirmation) {
                    this.passwordConfirmError = true;
                    return;
                }
                this.$http.post("/api/v1/registration", this.user)
                    .then((res) => {
                        window.location.href = '/';
                    })
                    .catch((errCode => {
                        console.log(errCode);
                    }))
            }
        }
    }
</script>

<style>
    .reg-form {
        display: flex;
        font-family: Arial;
        flex-direction: column;
        width: 400px;
        justify-content: space-between;
        position: fixed;
        top: 50%;
        left: 50%;
        /* bring your own prefixes */
        transform: translate(-60%, -60%);
    }

    .reg-btn {
        width: 200px;
    }

    .form-btns {
        display: flex;
        font-family: Arial;
        flex-direction: column;
    }

    .error-label {
        color: red;
    }
</style>