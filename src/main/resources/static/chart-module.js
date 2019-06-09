import Vue from 'vue';
import VueResource from 'vue-resource';
import 'lodash';
import chart from './chart.vue'
import {Line, mixins} from 'vue-chartjs'

Vue.use(Line);
Vue.use(mixins);
Vue.use(VueResource);

Vue.component("chart", chart);

new Vue({});