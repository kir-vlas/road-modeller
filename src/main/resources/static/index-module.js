import Vue from 'vue';
import VueResource from 'vue-resource';
import index from './index.vue'
import VueNativeSock from 'vue-native-websocket'
import VueMaterial from 'vue-material'
import 'vue-material/dist/vue-material.min.css'
import 'vue-material/dist/theme/black-green-light.css'

Vue.use(VueMaterial);
Vue.use(VueResource);


Vue.use(VueNativeSock, `wss://${window.location.host}/event-tick`);

Vue.component("index", index);

new Vue({
    el: '#main-container'
});