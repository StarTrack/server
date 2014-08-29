/** @jsx React.DOM */
var MainPage = React.createClass({
  getInitialState:function(){
      return {
        //login : 0 do not know, 1 not logged, 2 logged
        login:0
      };
  },
  render:function(){
    var currentPage = <span>loading</span>;
    if(this.state.login === 1) currentPage = <Home/>;
    if(this.state.login === 2) currentPage = <Cockpit/>;
    return <div>
      <Header/>
      {currentPage}
    </div>
  } 
});
