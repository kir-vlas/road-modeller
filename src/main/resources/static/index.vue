<template>
    <div class="container">
        <div class="main-panel">
            <div class="header">
                <h2>Моделирование движения транспорта</h2>
                <div>
                    <form method="post" action="/logout">
                        <md-button type="submit">Выход</md-button>
                    </form>

                </div>
            </div>
            <div class="manipulation">
                <div>
                    <md-button class="md-raised init-button" @click="settingsPanel = !settingsPanel">
                        {{!this.settingsPanel ? 'Создать модель' : 'Закрыть'}}
                    </md-button>
                    <md-button :disabled="active" class="md-raised btn" @click="loadModels">
                        {{!this.modelList ? 'Показать' : 'Скрыть'}} сохраненные
                    </md-button>
                    <md-button class="md-raised md-accent model-button" v-if="id" @click="model">
                        {{!this.active ? "Начать моделирование": "Приостановить"}}
                    </md-button>
                </div>
                <div class="models-panel">
                    <div v-if="modelList" class="models-list">
                        <div class="model-continue" v-for="modelId of unfinishedModels">
                            Идентификатор модели: {{modelId.id}}
                            <div>
                                <md-button class="md-raised md-accent" @click="execute(modelId.id)">Продолжить
                                </md-button>
                                <md-button class="md-raised md-primary" @click="deleteModel(modelId.id)">Удалить
                                </md-button>
                            </div>
                        </div>
                        <div v-if="!unfinishedModels.length">
                            Отсутствуют сохраненные модели
                        </div>
                    </div>
                </div>
                <div class="inputs">
                    <md-field>
                        <label> Идентификатор модели </label>
                        <md-input name="model-id" class="model-id" v-model="id"></md-input>
                    </md-field>
                </div>
                <div class="inputs">
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
            <div v-if="settingsPanel" class="model-settings">
                <div v-if="!settingsString">
                    <div class="model-settings-wrap" v-show="!modelSettings.isNotInitialized">
                        <div>
                            <h4>Дороги</h4>
                            <div class="roads-list">
                                <div class="road-lane-panel" v-for="roadLane of modelSettings.network">
                                    <md-field>
                                        <md-input placeholder="Длина" v-model="roadLane.length"></md-input>
                                    </md-field>
                                    <label>
                                        <md-checkbox v-model="roadLane.horizontal">Горизонтальная</md-checkbox>
                                    </label>
                                    <md-field>
                                        <md-input placeholder="Скоростной лимит"
                                                  v-model="roadLane.maxSpeedLimit"></md-input>
                                    </md-field>
                                    <md-field>
                                        <label>Плотность движения {{roadLane.trafficGeneratorFactor}}</label>
                                            <md-input v-model="roadLane.trafficGeneratorFactor"></md-input>
                                    </md-field>
                                    <div v-for="coord of roadLane.coordinates">
                                        <md-field>
                                            <label>Координата X</label>
                                            <md-input placeholder="Координата X" v-model="coord.x"></md-input>
                                        </md-field>
                                        <md-field>
                                            <label>Координата Y</label>
                                            <md-input placeholder="Координата Y" v-model="coord.y"></md-input>
                                        </md-field>
                                    </div>
                                </div>
                            </div>
                            <md-button class="md-raised btn" v-on:click="addRoad">Добавить дорогу</md-button>
                        </div>
                        <div>
                            <h4>Светофоры</h4>
                            <div class="roads-list">
                                <div class="road-lane-panel" v-for="trafficLight of modelSettings.trafficLights">
                                    <md-field>
                                        <md-input placeholder="Начальный статус"
                                                  v-model="trafficLight.status"></md-input>
                                    </md-field>
                                    <md-field>
                                        <md-input placeholder="Длительность красного сигнала"
                                                  v-model="trafficLight.redDelay"></md-input>
                                    </md-field>
                                    <md-field>
                                        <md-input placeholder="Длительность зеленого сигнала"
                                                  v-model="trafficLight.greenDelay"></md-input>
                                    </md-field>
                                    <md-field>
                                        <md-input placeholder="Координата X"
                                                  v-model="trafficLight.coordinates.x"></md-input>
                                    </md-field>
                                    <md-field>
                                        <md-input placeholder="Координата Y"
                                                  v-model="trafficLight.coordinates.y"></md-input>
                                    </md-field>
                                </div>
                            </div>
                            <md-button class="md-raised btn" v-on:click="addLight">Добавить светофор</md-button>
                        </div>
                        <div>
                            <h4>Длительность моделирования</h4>
                            <md-field>
                                <label>Продолжительность</label>
                                <md-input class="settings-input" v-model="modelSettings.maxDuration"></md-input>
                            </md-field>
                            <md-field>
                                <label>Изменение времени</label>
                                <md-input class="settings-input" v-model="modelSettings.timeDelta"></md-input>
                            </md-field>
                        </div>
                    </div>
                    <div class="factor-form" v-show="modelSettings.isNotInitialized">
                        <div v-show="!modelSettings.isDynamicFactor">
                            Плотность потока
                            <div>
                                <label>Дорога 1: {{modelSettings.trafficGenerate.road1}}</label>
                                <div class="traffic-slider">
                                    <input type="range"
                                           class="slider"
                                           name="freq"
                                           min="5"
                                           max="20"
                                           v-model="modelSettings.trafficGenerate.road1"/>
                                </div>
                            </div>
                            <div>
                                <label>Дорога 2: {{modelSettings.trafficGenerate.road2}}</label>
                                <div class="traffic-slider">
                                    <input type="range"
                                           class="slider"
                                           name="freq"
                                           min="5"
                                           max="20"
                                           v-model="modelSettings.trafficGenerate.road2"/>
                                </div>
                            </div>
                            <div>
                                <label>Дорога 3: {{modelSettings.trafficGenerate.road3}}</label>
                                <div class="traffic-slider">
                                    <input type="range"
                                           class="slider"
                                           name="freq"
                                           min="5"
                                           max="20"
                                           v-model="modelSettings.trafficGenerate.road3"/>
                                </div>
                            </div>
                            <div>
                                <label>Дорога 4: {{modelSettings.trafficGenerate.road4}}</label>
                                <div class="traffic-slider">
                                    <input type="range"
                                           class="slider"
                                           name="freq"
                                           min="5"
                                           max="20"
                                           v-model="modelSettings.trafficGenerate.road4"/>
                                </div>
                            </div>
                            <div>
                                <label>Дорога 5: {{modelSettings.trafficGenerate.road5}}</label>
                                <div class="traffic-slider">
                                    <input type="range"
                                           class="slider"
                                           name="freq"
                                           min="5"
                                           max="20"
                                           v-model="modelSettings.trafficGenerate.road5"/>
                                </div>
                            </div>
                            <div>
                                <label>Дорога 6: {{modelSettings.trafficGenerate.road6}}</label>
                                <div class="traffic-slider">
                                    <input type="range"
                                           class="slider"
                                           name="freq"
                                           min="5"
                                           max="20"
                                           v-model="modelSettings.trafficGenerate.road6"/>
                                </div>
                            </div>
                            <div>
                                <label>Дорога 7: {{modelSettings.trafficGenerate.road7}}</label>
                                <div class="traffic-slider">
                                    <input type="range"
                                           class="slider"
                                           name="freq"
                                           min="5"
                                           max="20"
                                           v-model="modelSettings.trafficGenerate.road7"/>
                                </div>
                            </div>
                            <div>
                                <label>Дорога 8: {{modelSettings.trafficGenerate.road8}}</label>
                                <div class="traffic-slider">
                                    <input type="range"
                                           class="slider"
                                           name="freq"
                                           min="5"
                                           max="20"
                                           v-model="modelSettings.trafficGenerate.road8"/>
                                </div>
                            </div>
                        </div>
                    </div>
                    <label>
                        <md-checkbox v-model="modelSettings.isNotInitialized">Стандартный сценарий</md-checkbox>
                    </label>
                    <label>
                        <md-checkbox v-model="modelSettings.isFlex">Адаптивный светофор</md-checkbox>
                    </label>
                    <label>
                        <md-checkbox v-model="modelSettings.isDynamicFactor">Диначеское изменение трафика</md-checkbox>
                    </label>
                </div>
                <md-field v-if="settingsString">
                    <md-textarea md-autogrow v-model="settingsString"></md-textarea>
                </md-field>
                <div>
                    <md-button class="md-raised" :disabled="modelSettings.isNotInitialized" @click="showSettings">
                        Экспорт настроек
                    </md-button>
                    <md-button class="md-raised md-accent" @click="init">Принять</md-button>
                </div>

            </div>
            <div v-if="shortStatistic">
                {{`Среднее количество автомобилей, стоящих на светофоре: ${shortStatistic.averageWaitingCars}`}}
            </div>
        </div>
        <div class="updating" v-show="shortStatistic">
            <md-field>
                <label>Длительность красного светофора</label>
                <md-input :disabled="!active" v-model="settingsUpdate.redDelay"></md-input>
            </md-field>
            <md-field>
                <label>Длительность зеленого светофора</label>
                <md-input :disabled="!active" v-model="settingsUpdate.greenDelay"></md-input>
            </md-field>
            <md-switch :disabled="!active" v-model="settingsUpdate.isAdaptive">
                {{ !settingsUpdate.isAdaptive ? 'Включить' : 'Отключить' }} адаптивный режим светофоров
            </md-switch>

            <md-button class="md-raised md-accent" @click="changeLights">Применить</md-button>
        </div>
        <div class="main-model" v-show="shortStatistic">
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
                unfinishedModels: [],
                statsChart: null,
                options: {
                    responsive: true,
                    animation: false,
                    scales: {
                        yAxes: [{
                            ticks: {
                                max: 10,
                                min: 0
                            }
                        }],
                        xAxes: [{
                            ticks: {
                                max: 30,
                                min: 30
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
                settingsUpdate: {
                    redDelay: 300,
                    greenDelay: 300,
                    isAdaptive: false,
                },
                modelSettings: {
                    drivers: [],
                    network: [{coordinates: [{}, {}]}],
                    trafficLights: [],
                    trafficGenerate: {
                        road1: 7,
                        road2: 7,
                        road3: 7,
                        road4: 7,
                        road5: 7,
                        road6: 7,
                        road7: 7,
                        road8: 7,
                    },
                    attributes: [],
                    maxDuration: 0,
                    timeDelta: 0,
                    isNotInitialized: true,
                    isFlex: false,
                    isDynamicFactor: false,
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
            renderChart: function () {
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
            changeLights: function () {
                this.model();
                this.$http.put(`/api/v1/models/${this.id}/update`, this.settingsUpdate)
                    .then(() => this.model())
            },
            logout: function () {
                window.location.href = '/logout';
            },
            execute: function (modelId) {
                this.id = modelId;
                this.modelList = false;
                this.model();
            },
            getShortStatistic: function () {
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
                        console.log(this.unfinishedModels)
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
                this.settingsUpdate.isAdaptive = this.modelSettings.isFlex;
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
            showSettings: function () {
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
                        this.provider.context.fillRect(driver.crd.x, driver.crd.y, 8, 8)
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
            },
            changeFactor: function () {

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
        flex-wrap: wrap;
        padding-top: 15px;
        padding-left: 30px;
    }

    .model-settings-wrap {
        display: flex;
        flex-wrap: wrap;
    }

    .main-panel {
        width: 1100px;
        display: flex;
        flex-direction: column;
        justify-content: center;
    }

    .header {
        display: flex;
        justify-content: space-between;
    }

    .updating {
        width: 500px;
        display: flex;
        flex-direction: column;
    }

    .manipulation {
        display: flex;
        flex-direction: column;
    }

    .factor-form {
        width: 300px;
        display: flex;
        flex-direction: column;
    }

    .model-id {
        width: 300px;
    }

    .render {
        display: flex;
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
        width: 1700px;
    }

    .slider-container {
        width: 500px;
    }

    .traffic-slider {
        width: 250px;
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
        padding: 5px;
    }

    .btn {
        width: 200px;
    }

    .roads-list {
        display: flex;
        flex-wrap: wrap;
    }

    .models-list {
        display: flex;
        flex-direction: column;
        width: 800px;
    }

    .model-continue {
        display: flex;
        justify-content: space-between;
        align-items: center;
    }

</style>