const merge = require('webpack-merge');
const path = require('path');
const autoprefixer = require('autoprefixer');
const postcssNormalize = require('postcss-normalize');
const baseConfig = require('./base.config.js');

module.exports = merge.smart(baseConfig, {
    output: {
        path: path.resolve(__dirname, '../resources/static/resources/js'),
        filename: 'bundle.js',
    },
    module: {
        rules: [
            {
                test: /\.scss$/,
                use: [
                    {
                        loader: 'file-loader',
                        options: {
                            name: 'bundle.css',
                            outputPath: '../css',
                        },
                    },
                ],
            },
        ],
    },
});