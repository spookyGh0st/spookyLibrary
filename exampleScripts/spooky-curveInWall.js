/*
	Our aim with this function is to edit notes in an order of blue down, red down, blue down, blue up.
*/


// This is our function declaration, always have to start with EXACTLY this, or whatever it may be in the future.


const myLib = require("./spookyLibrary/spookyScripts");

module.exports.run = function (cursor, notes, events, walls, save, global, data)
{
	// Always gotta have this output variable so MM can actually do something
	var output = {};
	output.message= "generating Curve";

	// Loop through our notes and edit them all!
	let finalWalls = [];
	try{
		for (let i = 0; i < walls.length; i++) {
			let spookyWall = myLib.SpookyWall_init_za3rmp$(walls[i]);
			let a = spookyWall.curveInWall_za3lpa$(1/JSON.stringify(data.cursorPrecision));
			for(let j =0; j<a.length;j++){
				finalWalls.push(a[j].toWall())
			}
		}
	}catch (e) {
		output.message = e.message
	}

		// Now we send all our data to the outputs
	output.notes = notes;
	output.events = events;
	output.walls = finalWalls;
	output.select = true;

	// and we can return here
	return output;
};
