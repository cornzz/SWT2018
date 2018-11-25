const webpack = require('webpack');
const path = require('path');
const glob = require('glob');
const autoprefixer = require('autoprefixer');
const postcssNormalize = require('postcss-normalize');

console.log('NODE_ENV: ' + process.env.NODE_ENV);

module.exports = {
    entry: ['./src/app.scss', './src/app.js'].concat(glob.sync("./src/js/mdc/*.js")),
    module: {
        rules: [
            {
                test: /\.scss$/,
                use: [
                    {
                        loader: 'file-loader',
                        options: {
                            name: 'bundle.css',
                        },
                    },
                    {
                        loader: 'extract-loader'
                    },
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
            },
        ],
    },
    plugins: [
        new webpack.EnvironmentPlugin([
            'NODE_ENV',
        ]),
    ],
};
