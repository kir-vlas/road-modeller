<template>
    <div class="container">
        <div class="reg-form">
            <label>
                Логин:
                <el-input type="text" v-model="user.username"/>
            </label>
            <label>
                Пароль:
                <el-input type="password" v-model="user.password"/>
            </label>
            <label>
                Имя:
                <el-input type="text" v-model="user.firstName"/>
            </label>
            <label>
                Фамилия:
                <el-input type="text" v-model="user.lastName"/>
            </label>
            <label>
                Отчество:
                <el-input type="text" v-model="user.middleName"/>
            </label>
        </div>
        <button class="reg-btn" v-on:click="register">Регистрация</button>
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
                }

            }
        },
        methods: {
            register: function () {
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
    .reg-form{
        display: flex;
        flex-direction: column;
        width: 400px;
        justify-content: space-between;
    }

    .reg-btn{
        width: 200px;
    }
</style>