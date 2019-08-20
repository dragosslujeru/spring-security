var http = require('http');
var fs = require('fs');
var server = http.createServer(function(request, response) {  
    fs.readFile("index.html", function(error, data) {  
                    if (error) {  
                        response.writeHead(404);  
                        response.write(error);  
                        response.end();  
                    } else {  
                        response.writeHead(200, {  
                            'Content-Type': 'text/html'  
                        });  
                        response.write(data);  
                        response.end();  
                    }  
                });   
});  
server.listen(8082);