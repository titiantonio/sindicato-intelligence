const fs = require('fs');
const path = require('path');

function primeiconsFontsMiddleware() {
  const mimeTypes = {
    '.woff2': 'font/woff2',
    '.woff': 'font/woff',
    '.ttf': 'font/ttf'
  };

  return function (request, response, next) {
    const match = request.url.match(/^\/base\/media\/(primeicons\.(?:woff2|woff|ttf))(?:\?.*)?$/);

    if (!match) {
      next();
      return;
    }

    const fontPath = path.join(__dirname, 'node_modules', 'primeicons', 'fonts', match[1]);

    fs.readFile(fontPath, (error, content) => {
      if (error) {
        next();
        return;
      }

      response.writeHead(200, {
        'Content-Type': mimeTypes[path.extname(fontPath)] || 'application/octet-stream'
      });
      response.end(content);
    });
  };
}

module.exports = function (config) {
  config.set({
    basePath: '',
    frameworks: ['jasmine'],
    plugins: [
      require('karma-jasmine'),
      require('karma-chrome-launcher'),
      require('karma-jasmine-html-reporter'),
      require('karma-coverage'),
      {
        'middleware:primeiconsFonts': ['factory', primeiconsFontsMiddleware]
      }
    ],
    client: {
      jasmine: {}
    },
    reporters: ['progress', 'kjhtml'],
    browsers: ['ChromeHeadless'],
    restartOnFileChange: true,
    middleware: ['primeiconsFonts']
  });
};
