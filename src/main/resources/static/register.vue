<template>
    <div class="container">
        <div class="reg-form">
            <label>
                <span>Логин:</span>
                <el-input type="text" v-model="user.username"/>
            </label>
            <label>
                <span>Пароль:</span>
                <el-input type="password" v-model="user.password"/>
            </label>
            <label>
                <span>Подтверждение пароля:</span>
                <el-input type="password" v-model="passwordConfirmation"/>
            </label>
            <label>
                <span>Имя:</span>
                <el-input type="text" v-model="user.firstName"/>
            </label>
            <label>
                <span>Фамилия:</span>
                <el-input type="text" v-model="user.lastName"/>
            </label>
            <label>
                <span>Отчество:</span>
                <el-input type="text" v-model="user.middleName"/>
            </label>
        </div>
        <div class="form-btns">
            <span class="error-label" v-if="error">Ошибка: пароли не совпадают</span>
            <button class="reg-btn" v-on:click="register">Регистрация</button>
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
                error: false,

            }
        },
        methods: {
            register: function () {
                if (!this.user.password || this.user.password !== this.passwordConfirmation) {
                    this.error = true;
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
    }

    .reg-btn {
        width: 200px;
    }

    .form-btns{
        display: flex;
        font-family: Arial;
        flex-direction: column;
    }

    .error-label{
        color: red;
    }
</style>