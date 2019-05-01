<template>
    <div>
        <h2>HW!</h2>
        <button @click="clickButton">43643</button>
        <textarea v-model="val"></textarea>
    </div>
</template>

<script>
    export default {
        name: 'index',
        data() {
            return {
                val: "123",
                active: false,
                num: 123
            }
        },
        created() {
            this.$options.sockets.onmessage = (data) => {
                console.log(data);
                this.val = JSON.parse(data.data).uuid
            }
        },
        methods: {
            clickButton: function () {
                this.active = !this.active;
                if (!this.active) {
                    clearInterval(this.num);
                } else {
                    this.num = setInterval(() =>  this.$socket.send(this.val), 1000);
                }
            }
        }
    }
</script>