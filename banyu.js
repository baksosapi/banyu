var aql = require('alasql');
var http = require("http");



http.get("https://raw.githubusercontent.com/onaio/ona-tech/master/data/water_points.json", function(response){
    //console.log(response.statusCode);

    var body = '';

    response.on('data', function(chunk) {
        body += chunk;
    });

    response.on('end', function() {
        var data = JSON.parse(body);
        // console.log(data);

        // var recap = aql("SELECT ROW _ FROM ?",[data]);
        var func = aql("SELECT * FROM ? WHERE water_functioning == 'yes'", [data]);
        var res = aql("SELECT * FROM ?",[data]);
        var water_fn = aql("SELECT DISTINCT water_functioning as water_fn , COUNT(*) as number_fn FROM " +
            "? GROUP BY water_functioning", [data]);
        var comm = aql("SELECT DISTINCT communities_villages as village_name , COUNT(*) as number_water FROM " +
            "? GROUP BY communities_villages", [data]);
        var Bbroken = aql("SELECT communities_villages, COUNT(water_not_functioning) as nf FROM ? " +
            "GROUP BY communities_villages ORDER BY nf ", [data]);

        var max = aql("SELECT MAX(nf) FROM ?", [Bbroken]);
        var maxx = max.valueOf();
        var rank = aql("SELECT distinct(nf) as rank FROM ?", [Bbroken]);

        var result = {
            // dis : recap
            num_water_points: res.length,
            water_fn: water_fn,
            number_functional: func.length,
            community: comm,
            water_points_broken: Bbroken,
            rank: rank,

        }

        console.log(result)

    });

});