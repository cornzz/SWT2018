const webpack = require('webpack');
const path = require('path');
const glob = require('glob');
const autoprefixer = require('autoprefixer');
const postcssNormalize = require('postcss-normalize');
const ExtractTextPlugin = require('extract-text-webpack-plugin');

console.log('NODE_ENV: ' + process.env.NODE_ENV);

module.exports = {
    entry: ['./src/app.scss', './src/app.js'].concat(glob.sync("./src/js/mdc/*.js")),
    module: {
        rules: [
            {
                test: /\.scss$/,
                use: ExtractTextPlugin.extract({
                    fallback: 'style-loader',
                    use: [
                        {
                            loader: 'css-loader'
                        },
                        {
                            loader: 'postcss-loader',
                            options: {
                                plugins: () => [
                                    autoprefixer(),
                                    postcssNormalize({
                                        browsers: 'last 2 versions'
                                    })
                                ],
                            },
                        },
                        {
                            loader: 'sass-loader',
                            options: {
                                includePaths: [path.resolve(__dirname, 'node_modules')],
                            },
                        },
                    ],
                }),
            },
        ],
    },
    plugins: [
        new webpack.EnvironmentPlugin([
            'NODE_ENV',
        ]),
    ],
};
