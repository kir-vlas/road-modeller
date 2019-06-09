<template>
    <div class="container">
        <h2>Моделирование движения транспорта</h2>
        <div class="manipulation">
            <button class="init-button" @click="settingsPanel = !settingsPanel">Создать модель</button>
            <button class="model-button" v-if="id" @click="model">
                {{!this.active ? "Запустить моделирование": "Остановить моделирование"}}
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
                        <input type="range"
                               class="slider"
                               name="freq"
                               min="1"
                               max="100"
                               v-on:click="changeSpeed"
                               v-model="freq"/>
                    </div>
                </label>
            </div>
            <span v-if="cars">Количество машин на дороге: {{this.cars}}</span>
            <span v-if="time">Прогресс: {{this.time}} / {{this.maxDuration}}</span>
        </div>
        <hr>
        <div v-if="!active" class="models-panel">
            <button class="btn" @click="loadModels">Показать незавершенные</button>
            <div v-if="modelList" class="models-list">
                <div class="model-continue" v-for="modelId of unfinishedModels">
                    Идентификатор модели: {{modelId.id}}}
                    <div>
                        <button @click="execute(modelId.id)">Продолжить</button>
                        <button @click="deleteModel(modelId.id)">Удалить</button>
                    </div>
                </div>
            </div>
        </div>
        <div v-if="settingsPanel" class="model-settings">
            <label>
                Стандартный сценарий
                <input type="checkbox" v-model="modelSettings.isNotInitialized"/>
                <button @click="init">Принять</button>
            </label>
            <div v-show="!modelSettings.isNotInitialized">
                <button @click="showSettings">Экспорт настроек</button>
                <textarea class="model-init-settings" v-if="settingsString" v-model="settingsString"></textarea>
                <h4>Дороги</h4>
                <div class="roads-list">
                    <div class="road-lane-panel" v-for="roadLane of modelSettings.network">
                        <input type="text" placeholder="Длина" v-model="roadLane.length"/>
                        <label>
                            Горизонтальная
                            <input type="checkbox" v-model="roadLane.horizontal"/>
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
                </div>

                <button class="btn" v-on:click="addRoad">Добавить дорогу</button>
                <h4>Светофоры</h4>
                <div class="roads-list">
                    <div class="road-lane-panel" v-for="trafficLight of modelSettings.trafficLights">
                        <input placeholder="Начальный статус" v-model="trafficLight.status"/>
                        <input placeholder="Длительность красного сигнала" v-model="trafficLight.redDelay"/>
                        <input placeholder="Длительность зеленого сигнала" v-model="trafficLight.greenDelay"/>
                        <input placeholder="Координата X" v-model="trafficLight.coordinates.x"/>
                        <input placeholder="Координата Y" v-model="trafficLight.coordinates.y"/>
                    </div>
                </div>25
                <button class="btn" v-on:click="addLight">Добавить светофор</button>
                <h4>Длительность моделирования</h4>
                <input class="settings-input" placeholder="Максимальная продолжительность"
                       v-model="modelSettings.maxDuration"/>
                <input class="settings-input" placeholder="Изменение времени" v-model="modelSettings.timeDelta"/>
            </div>
        </div>
        <div v-if="shortStatistic">
            {{`Среднее количество автомобилей, стоящих на светофоре: ${shortStatistic.averageWaitingCars}`}}
        </div>
        <div v-if="active">
            <label>
                Длительность красного светофора
                <input v-model="redDelay"/>
            </label>
            <label>
                Длительность зеленого светофора
                <input v-model="greenDelay"/>
            </label>
            <button @click="changeLights">Применить</button>
        </div>
        <div class="main-model">
            <div class="render">
                <canvas ref="model-container"></canvas>
            </div>
            <div class="stats">
                <chart v-if="statsChart" :chart-data="statsChart" :options="options"></chart>
            </div>
        </div>
    </div>
</template>

<script>
    import chart from './chart.vue';

    export default {
        name: 'index',
        components: {chart},
        data() {
            return {
                active: false,
                settingsPanel: false,
                modelList: false,
                redDelay: 0,
                greenDelay: 0,
                unfinishedModels: [],
                statsChart: null,
                options: {
                    responsive: true,
                    animation: false,
                    scales: {
                        yAxes : [{
                            ticks : {
                                max : 10,
                                min : 0
                            }
                        }],
                        xAxes : [{
                            ticks : {
                                max : 30,
                                min : 30
                            }
                        }]
                    }
                },
                modelIntervalNumber: 0,
                statsIntervalNumber: 0,
                freq: 30,
                provider: {
                    context: null
                },
                id: "",
                stats: "",
                settingsString: '',
                modelSettings: {
                    drivers: [],
                    network: [{coordinates: [{}, {}]}],
                    trafficLights: [],
                    attributes: [],
                    maxDuration: 0,
                    timeDelta: 0,
                    isNotInitialized: false,
                    isFlex: true,
                },
                shortStatistic: null,
                cars: 0,
                maxDuration: 0,
                time: 0,
                carImage: null,
            }
        },
        created() {
            this.carImage = new Image();
            this.carImage.src = '/static/car.svg';
            this.$options.sockets.onmessage = (data) => {
                const modelState = JSON.parse(data.data);
                let isCompleted = modelState.isCompleted;
                if (isCompleted) {
                    this.cars = 0;
                    this.getStats();
                    this.active = false;
                    console.log("END");
                    clearInterval(this.modelIntervalNumber);
                    clearInterval(this.statsIntervalNumber);
                }
                this.render(modelState);
            };
            this.$options.sockets.onclose = () => {
                this.active = false;
                clearInterval(this.modelIntervalNumber);
                clearInterval(this.statsIntervalNumber);
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
                    clearInterval(this.modelIntervalNumber);
                    clearInterval(this.statsIntervalNumber);
                } else {
                    this.modelIntervalNumber = setInterval(() => this.$socket.send(this.id), 1000 / this.freq);
                    this.statsIntervalNumber = setInterval(() => this.getShortStatistic(), 1000);
                }
            },
            renderChart: function() {
                const dataset = this.shortStatistic.averageWaitingTime.slice(-30);
                this.statsChart = {
                    labels: new Array(30),
                    datasets: [
                        {
                            label: 'Среднее количество машин на светофоре',
                            borderColor: '#44b900',
                            borderWidth: 1,
                            data: dataset,
                        }
                    ]
                }
            },
            changeLights: function() {
                this.model();
                this.$http.get(`/api/v1/models/lights/${this.id}?redDelay=${this.redDelay}&greenDelay=${this.greenDelay}`)
                    .then(() => this.model() )
            },
            execute: function (modelId) {
                this.id = modelId;
                this.modelList = false;
                this.model();
            },
            getShortStatistic: function() {
                this.$http.get(`/api/v1/models/${this.id}/short`)
                    .then((res) => {
                        this.shortStatistic = res.body;
                        this.renderChart();
                    })
            },
            deleteModel: function (modelId) {
                this.$http.delete(`/api/v1/models/${modelId}`);
                this.$http.get("/api/v1/models")
                    .then(res => {
                        this.unfinishedModels = res.body;
                    });
            },
            getStats: function () {
                this.$http.get(`/api/v1/models/${this.id}`)
                    .then(res => {
                        this.stats = res.body;
                    });
            },
            loadModels: function () {
                this.modelList = !this.modelList;
                if (this.modelList) {
                    this.$http.get("/api/v1/models")
                        .then(res => {
                            this.unfinishedModels = res.body;
                        });
                }
            },
            init: function () {
                if (this.active) {
                    clearInterval(this.modelIntervalNumber);
                    this.active = false;
                }
                this.settingsPanel = false;
                this.$http.post("/api/v1/models/init", this.modelSettings)
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
            showSettings: function() {
                if (this.settingsString) {
                    this.modelSettings = JSON.parse(this.settingsString);
                    this.settingsString = '';
                } else {
                    this.settingsString = JSON.stringify(this.modelSettings, null, 3);
                }
            },
            render: function (modelState) {
                this.provider.context.fillStyle = "#dbebb9";
                this.provider.context.fillRect(0, 0, 950, 1000);
                modelState.network.forEach((roadLane) => {
                    const coords = roadLane.crds;
                    this.provider.context.strokeStyle = "white";
                    if (roadLane.hor) {
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
                        this.provider.context.fillStyle = light.l.toLowerCase();
                        this.provider.context.fillRect(light.crd.x, light.crd.y, 5, 5);
                    });
                }
                if (modelState.drivers) {
                    this.cars = modelState.drivers.length;
                    modelState.drivers.forEach((driver) => {
                        this.provider.context.fillStyle = "#8597eb";
                        this.provider.context.fillRect(driver.crd.x, driver.crd.y, 10, 10)
                    });
                }
                this.time = modelState.time;
                this.maxDuration = modelState.maxDuration;
            },
            changeSpeed: function () {
                if (!this.active) {
                    return;
                }
                clearInterval(this.modelIntervalNumber);
                this.modelIntervalNumber = setInterval(() => this.$socket.send(this.id), 1000 / this.freq);
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

    .roads-list {
        display: flex;
        flex-wrap: wrap;
    }

    .model-init-settings {
        width: 800px;
        height: 1000px;
        overflow: scroll;
    }

    .models-list {
        display: flex;
        flex-direction: column;
    }

    .model-continue {
        display: flex;
        justify-content: space-between;
    }

    .models-panel {
        display: flex;
        flex-direction: column;
        width: 800px;
    }
</style>