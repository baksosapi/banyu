var aql = require('alasql');
var request = require('request');


var url="https://raw.githubusercontent.com/onaio/ona-tech/master/data/water_points.json";

function stats(url, callback){
    // Make a request Http to server
    request({url:url, json:true}, function (err, resp, jsonData) {
        // Check if connection is OK
        if (err || resp.statusCode !== 200) {
            console.log('Failed to retrieve Ona water data.');
            return;
        }

        // Using alasql to proceed jsondata

        // var recap = aql("SELECT ROW _ FROM ?",[data]);
        var func = aql("SELECT * FROM ? WHERE water_functioning == 'yes'", [jsonData]);
        var res = aql("SELECT * FROM ?",[jsonData]);
        var water_fn = aql("SELECT DISTINCT water_functioning as water_fn , COUNT(*) as number_fn FROM " +
            "? GROUP BY water_functioning", [jsonData]);
        var comm = aql("SELECT DISTINCT communities_villages as village_name , COUNT(*) as number_water FROM " +
            "? GROUP BY communities_villages", [jsonData]);
        var Bbroken = aql("SELECT communities_villages, COUNT(water_not_functioning) as nf FROM ? " +
            "GROUP BY communities_villages ORDER BY nf ", [jsonData]);
        var rank = aql("SELECT DISTINCT(nf) as nf FROM ?", [Bbroken]);

        // get Ranks
        var rankss = toArray(rank);

        // get Result to display

        var result = {
            // dis : recap

            num_water_points: res.length,
            water_fn: water_fn,
            number_functional: func.length,
            community: comm,
            water_points_broken: Bbroken,
            rank: rank,
            rankings: rankings(rank),

        }

        // console.log(jsonData);

        return callback(err, result)
    });
}

function toArray(obj) {
    var propList= [];
    var result = [];
    var count = 0;
    for (var prop in obj) {

        // console.log(count++);

        var value = obj[prop];

        // console.log(value+" - "+typeof value+" - "+count++);

        if (typeof value === 'object') {
            // console.log(count+" is an object");
            // result.push(toArray(value)); // <- recursive call
            // propList.push(toArray(value)); // <- recursive call
            toArray(value); // <- recursive call
        }
        else {
            // if(!isExist(propList, obj.prop)){
            //     propList.push(obj.prop);
            // }
            // result.push(prop);
            propList.push(prop);
            console.log(propList);
        }
        count++;
        if(count===10) {count =0; break};
        // console.log(result)
    }
    result = auniq(propList);
    // return result;
    return propList;
}

function rankings(arr){

    var sorted = arr.slice().sort(function(a,b){return b-a})
    var ranks = arr.slice().map(function(v){ return sorted.indexOf(v)+1 });

    console.log(ranks)

}


stats(url, function(error, res) {
    //if there is an error show it else show the results
    if (error) {
        console.error(error);
    } else {
        console.log(res);
    }
});