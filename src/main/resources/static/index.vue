<template>
    <div>
        <h2>HW!</h2>
        <button @click="clickButton">43643</button>

        <input v-model="val"></input>
    </div>
</template>

<script>
    export default {
        name: 'index',
        data() {
            return {
            val: "123"
        }},
        created(){
            this.$options.sockets.onmessage = (data) => this.val = data.data
        },
        sockets: {
            data(data){
                console.log(data)
            },
            connect: function () {
                console.log('socket connected')
            },
            customEmit: function (data) {
                console.log('this method was fired by the socket server. eg: io.emit("customEmit", data)')
            }
        },
        methods: {
            clickButton: function () {
                this.$socket.send(this.val);
            }
        }
    }
</script>