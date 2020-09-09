const proxy = require('koa-proxies');

module.exports = {
    port: 8080,
    watch: true,
    nodeResolve: true,
    appIndex: 'index.html',
    plugins: [],
    middlewares: [
        proxy('/api', {
            target: 'http://localhost:8989',
        }),
    ],
};