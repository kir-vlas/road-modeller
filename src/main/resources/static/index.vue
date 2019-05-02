<template>
    <div>
        <h2>HW!</h2>
        <button @click="clickButton">model</button>
        <button @click="init">init</button>
        <textarea v-model="val"></textarea>
        <textarea v-model="id"></textarea>
    </div>
</template>

<script>
    export default {
        name: 'index',
        data() {
            return {
                val: "123",
                active: false,
                num: 123,
                id: ""
            }
        },
        created() {
            this.$options.sockets.onmessage = (data) => {
                console.log(data);
                let isCompleted = JSON.parse(data.data).isCompleted;
                if (isCompleted) {
                    this.active = false;
                    console.log("END");
                    clearInterval(this.num);
                }
                this.val = data.data
            };
            this.$options.sockets.onclose = (data) => {
                this.active = false;
                clearInterval(this.num);
            }
        },
        methods: {
            clickButton: function () {
                this.active = !this.active;
                if (!this.active) {
                    clearInterval(this.num);
                } else {
                    this.num = setInterval(() =>  this.$socket.send(this.id), 16);
                }
            },
            init: function () {
                this.$http.get("http://localhost:8090/init")
                    .then(res => {
                        this.id = res.body.id
                    });
            }
        }
    }
</script>