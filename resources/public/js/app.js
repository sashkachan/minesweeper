/* 
 components:
   Control bar
    * New game
    * Counters
   Game field
    * Cells
    
 */




var NewGameForm = React.createClass({
    render: function () {
	return (<div id="new_game">
		
		</div>);
    }
});

var ControlBar = React.createClass({
    render: function () {
	return(
		<div></div>
	);
    }
});

var Field = React.createClass({
    getInitialState: function () {
	return [];
    },
    render: function () {
	return(
		<div id="field"></div>
	);	
    }
});

var Cell = React.createClass({
    render: function () {
	return(
		<div className="cell"></div>
	);
    }
});

React.render(
	<div>
	<NewGameForm/>
	<Field/>
	</div>,
    document.getElementById("content")
);
