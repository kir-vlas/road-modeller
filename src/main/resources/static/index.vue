<template>
    <div>
        <h2>HW!</h2>
        <button @click="clickButton">model</button>
        <button @click="init">init</button>
        <label>
            <textarea v-model="id"></textarea>
        </label>
        <canvas ref="model-container"></canvas>
    </div>
</template>

<script>
    export default {
        name: 'index',
        data() {
            return {
                active: false,
                num: 123,
                provider: {
                    context: null
                },
                id: ""
            }
        },
        created() {
            this.$options.sockets.onmessage = (data) => {
                const modelState = JSON.parse(data.data);
                let isCompleted = modelState.isCompleted;
                if (isCompleted) {
                    this.active = false;
                    console.log("END");
                    clearInterval(this.num);
                }
                this.render(modelState);
            };
            this.$options.sockets.onclose = () => {
                this.active = false;
                clearInterval(this.num);
            }
        },
        mounted() {
            this.provider.context = this.$refs['model-container'].getContext('2d');
            this.$refs['model-container'].width = 5000;
            this.$refs['model-container'].height = 1000;
        },
        methods: {
            clickButton: function () {
                this.active = !this.active;
                if (!this.active) {
                    clearInterval(this.num);
                } else {
                    this.num = setInterval(() =>  this.$socket.send(this.id), 7);
                }
            },
            init: function () {
                this.$http.get("http://localhost:8090/init")
                    .then(res => {
                        this.id = res.body.id
                    });
            },
            render: function (modelState) {
                this.provider.context.clearRect(0, 0, 5000, 1000);
                modelState.network.forEach((roadLane) => {
                    const coords = roadLane.coordinates;
                    this.provider.context.strokeRect(coords[0].x, coords[0].y, coords[1].x, 10)
                });
                modelState.drivers.forEach((driver) => {
                    this.provider.context.strokeRect(driver.currentCoordinates.x, driver.currentCoordinates.y, 10, 10)
                });
            }
        }
    }
</script>