var hyperdocs = require("./hyperdocs");

hyperdocs.readKYCImage("./sample.jpg",
    { referenceId: 'my-reference-id' },
    { enableDashboard: 'yes' },
    function(err, data){
    console.log(err, JSON.stringify(data));
})
