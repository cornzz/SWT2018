const merge = require('webpack-merge');
const path = require('path');
const ExtractTextPlugin = require('extract-text-webpack-plugin');
const baseConfig = require('./base.config.js');

module.exports = merge.smart(baseConfig, {
    output: {
        path: path.resolve(__dirname, '../resources/static/resources/js'),
        filename: 'bundle.js',
    },
    plugins: [
        new ExtractTextPlugin({
            filename: '../css/bundle.css',
        }),
    ]
});