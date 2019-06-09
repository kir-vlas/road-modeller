var path = require('path');
var webpack = require('webpack');

var ExtractTextPlugin = require("extract-text-webpack-plugin");
var UglifyJSPlugin = require('uglifyjs-webpack-plugin');

const NODE_ENV = process.env.NODE_ENV;
console.log("BUILD MODE IS " + NODE_ENV);


const outputPath = './target/classes/static/dist';
const sourcesPath = './src/main/resources/static';

module.exports = {
    entry: {
        index: sourcesPath + '/index-module.js',
        register: sourcesPath + '/register-module.js',
        chart: sourcesPath + '/chart-module.js'
    },
    watch: NODE_ENV === 'development',
    output: {
        path: path.resolve(__dirname, outputPath),
        filename: '[name].js',
        library: "[name]"
    },
    module: {
        rules: [
            {
                test: /\.css$/,
                use: ExtractTextPlugin.extract({
                    fallback: "style-loader",
                    use: "css-loader"
                })
            },
            {
                test: /\.vue$/,
                loader: 'vue-loader',
                options: {
                    extractCSS: true
                }
            },
            {
                test: /\.(png|jpg|gif|svg)$/,
                loader: 'file-loader',
                options: {
                    name: '[name].[ext]?[hash]'
                }
            },
            {
                test: /\.(eot|svg|ttf|woff|woff2)(\?\S*)?$/,
                loader: 'file-loader'
            },
            {
                test: /\.js$/,
                exclude: /(node_modules|bower_components)/,
                use: {
                    loader: 'babel-loader',
                    options: {
                        "plugins": [["component",
                            {
                                "libraryName": "element-ui",
                                "styleLibraryName": "theme-chalk"
                            }
                        ]]
                    }
                }
            }
        ]
    },
    plugins: [
        new ExtractTextPlugin({
            filename: "[name].css"
        }),
        new webpack.NormalModuleReplacementPlugin(
            /element-ui[\/\\]lib[\/\\]locale[\/\\]lang[\/\\]zh-CN/,
            'element-ui/lib/locale/lang/ru-RU'),
        new webpack.DefinePlugin({
            'process.env': {
                NODE_ENV: JSON.stringify(NODE_ENV)
            }
        }),

    ],
    resolve: {
        alias: {
            'vue$': 'vue/dist/vue.esm.js'
        }
    },
    devServer: {
        historyApiFallback: true,
        noInfo: true
    },
    performance: {
        hints: false
    },
    devtool: '#eval-source-map'
};

if (NODE_ENV === 'production') {
    module.exports.devtool = 'none';
    module.exports.plugins = (module.exports.plugins).concat([
        new UglifyJSPlugin({
            sourceMap: false,
            parallel: true
        }),
        new webpack.optimize.ModuleConcatenationPlugin()
    ]);
}

