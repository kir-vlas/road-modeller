<template>
    <div class="container">
        <h2>Моделирование движения транспорта</h2>
        <div class="manipulation">
            <button class="init-button" @click="init">Создать модель</button>
            <button class="model-button" @click="model">{{!this.active ? "Запустить моделирование": "Остановить моделирование"}}
            </button>
            <div class="inputs">
                <label>
                    Идентификатор модели
                    <input name="model-id" class="model-id" v-model="id"/>
                </label>
                <label>
                    Частота:
                    {{freq}}
                    <div class="slider-container">
                        <input type="range" class="slider" name="freq" min="1" max="144" v-on:click="changeSpeed"
                               v-model="freq"/>
                    </div>
                </label>
            </div>
            <span v-if="cars">Количество машин на дороге: {{this.cars}}</span>
        </div>
        <hr>
        <div class="model-settings">
            <label>
                Стандартный сценарий
                <input type="checkbox" v-model="modelSettings.isNotInitialized"/>
            </label>
            <div v-show="!modelSettings.isNotInitialized">
                <h4>Дороги</h4>
                <div class="road-lane-panel" v-for="roadLane of modelSettings.network">
                    <input type="text" placeholder="Длина" v-model="roadLane.length"/>
                    <label>
                        Горизонтальная
                        <input type="checkbox" v-model="roadLane.isHorizontal"/>
                    </label>
                    <input type="text" placeholder="Скоростной лимит" v-model="roadLane.maxSpeedLimit"/>
                    <input type="text" placeholder="Частота движения" v-model="roadLane.trafficGeneratorFactor"/>
                    <div v-for="coord of roadLane.coordinates">
                        <label>
                            Координата X
                            <input placeholder="Координата X" v-model="coord.x"/>
                        </label>
                        <label>
                            Координата Y
                            <input placeholder="Координата Y" v-model="coord.y"/>
                        </label>
                    </div>
                </div>
                <button class="btn" v-on:click="addRoad">Добавить дорогу</button>
                <h4>Светофоры</h4>
                <div v-for="trafficLight of modelSettings.trafficLights">
                    <input placeholder="Начальный статус" v-model="trafficLight.status"/>
                    <input placeholder="Длительность красного сигнала" v-model="trafficLight.redDelay"/>
                    <input placeholder="Длительность зеленого сигнала" v-model="trafficLight.greenDelay"/>
                    <input placeholder="Координата X" v-model="trafficLight.coordinates.x"/>
                    <input placeholder="Координата Y" v-model="trafficLight.coordinates.y"/>
                </div>
                <button class="btn" v-on:click="addLight">Добавить светофор</button>
                <h4>Длительность моделирования</h4>
                <input class="settings-input" placeholder="Максимальная продолжительность"
                       v-model="modelSettings.maxDuration"/>
                <input class="settings-input" placeholder="Изменение времени" v-model="modelSettings.timeDelta"/>
            </div>
        </div>
        <div class="main-model">
            <div class="render">
                <canvas ref="model-container"></canvas>
            </div>
            <div class="stats">
                <textarea readonly class="stats-box">{{stats}}</textarea>
            </div>
        </div>
    </div>
</template>

<script>
    export default {
        name: 'index',
        data() {
            return {
                active: false,
                num: 0,
                freq: 60,
                provider: {
                    context: null
                },
                id: "",
                stats: "",
                modelSettings: {
                    drivers: [],
                    network: [{coordinates: [{}, {}]}],
                    trafficLights: [{coordinates: {}}],
                    attributes: [],
                    maxDuration: 0,
                    timeDelta: 0,
                    isNotInitialized: false
                },
                cars: 0
            }
        },
        created() {
            this.$options.sockets.onmessage = (data) => {
                const modelState = JSON.parse(data.data);
                let isCompleted = modelState.isCompleted;
                if (isCompleted) {
                    this.getStats();
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
            this.$refs['model-container'].width = 950;
            this.$refs['model-container'].height = 1000;
        },
        methods: {
            model: function () {
                if (!this.id) {
                    return;
                }
                this.active = !this.active;
                this.stats = "";
                if (!this.active) {
                    clearInterval(this.num);
                } else {
                    this.num = setInterval(() => this.$socket.send(this.id), 1000 / this.freq);
                }
            },
            getStats: function () {
                this.$http.get(`/model/${this.id}`)
                    .then(res => {
                        this.stats = res.body;
                    });
            },
            init: function () {
                if (this.active) {
                    clearInterval(this.num);
                    this.active = false;
                }
                this.$http.post("/init", this.modelSettings)
                    .then(res => {
                        this.id = res.body.id
                    });
            },
            addRoad: function () {
                this.modelSettings.network.push({coordinates: [{}, {}]});
            },
            addLight: function () {
                this.modelSettings.trafficLights.push({coordinates: {}});
            },
            render: function (modelState) {
                this.provider.context.fillStyle = "#dbebb9";
                this.provider.context.fillRect(0, 0, 950, 1000);
                modelState.network.forEach((roadLane) => {
                    const coords = roadLane.coordinates;
                    this.provider.context.strokeStyle = "white";
                    if (roadLane.horizontal) {
                        this.provider.context.beginPath();
                        this.provider.context.moveTo(coords[0].x, coords[0].y);
                        this.provider.context.lineTo(coords[0].x, coords[0].y + 10);
                        this.provider.context.lineTo(coords[1].x, coords[1].y + 10);
                    } else {
                        this.provider.context.beginPath();
                        this.provider.context.moveTo(coords[0].x, coords[0].y);
                        this.provider.context.lineTo(coords[0].x + 10, coords[0].y);
                        this.provider.context.lineTo(coords[1].x + 10, coords[1].y);
                    }
                    this.provider.context.lineTo(coords[1].x, coords[1].y);
                    this.provider.context.closePath();
                    this.provider.context.stroke();
                    this.provider.context.fillStyle = "#c6c1bf";
                    this.provider.context.fill();
                    this.provider.context.closePath();
                });
                if (modelState.trafficLights) {
                    modelState.trafficLights.forEach((light) => {
                        this.provider.context.strokeStyle = 'blue';
                        this.provider.context.fillStyle = light.status.toLowerCase();
                        this.provider.context.fillRect(light.coordinates.x, light.coordinates.y, 5, 5);
                    });
                }
                if (modelState.drivers) {
                    this.cars = modelState.drivers.length;
                    modelState.drivers.forEach((driver) => {
                        this.provider.context.fillStyle = "#8597eb";
                        this.provider.context.fillRect(driver.currentCoordinates.x, driver.currentCoordinates.y, 10, 10)
                    });
                }
            },
            changeSpeed: function () {
                if (!this.active) {
                    return;
                }
                clearInterval(this.num);
                this.num = setInterval(() => this.$socket.send(this.id), 1000 / this.freq);
            }
        }
    }
</script>

<style>
    body {
        width: 100%;
        font-family: Arial;
    }

    .container {
        display: flex;
        flex-direction: column;
        justify-content: center;
    }

    .manipulation {
        display: flex;
        flex-direction: column;
    }

    .model-id {
        width: 300px;
    }

    .render {
        display: flex;
        overflow: scroll;
        min-width: 1000px;
        min-height: 1000px;
        max-width: 1500px;
        max-height: 2000px;
    }

    .init-button {
        width: 200px;
    }

    .model-button {
        width: 200px;
    }

    .inputs {
        display: flex;
        width: 500px;
        justify-content: space-between;
    }

    .stats {
        width: 700px;
    }

    .stats-box {
        width: 700px;
        height: 993px;
    }

    .main-model {
        display: flex;
    }

    .slider-container {
        width: 500px;
    }

    .slider {
        -webkit-appearance: none;
        width: 100%;
        height: 15px;
        border-radius: 5px;
        background: #d3d3d3;
        outline: none;
        opacity: 0.7;
        -webkit-transition: .2s;
        transition: opacity .2s;
    }

    .slider::-webkit-slider-thumb {
        -webkit-appearance: none;
        appearance: none;
        width: 25px;
        height: 25px;
        border-radius: 50%;
        background: #4CAF50;
        cursor: pointer;
    }

    .slider::-moz-range-thumb {
        width: 25px;
        height: 25px;
        border-radius: 50%;
        background: #4CAF50;
        cursor: pointer;
    }

    .model-settings {
        display: flex;
        flex-direction: column;
    }

    .settings-input {
        width: 200px;
    }

    .road-lane-panel {
        display: flex;
        flex-direction: column;
        width: 300px;
    }

    .btn {
        width: 200px;
    }
</style>