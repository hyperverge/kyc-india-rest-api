var hyperdocs = require("./hyperdocs");

hyperdocs.readKYCImage("./sample.jpg", function(err, data){
    console.log(err, data);
})
