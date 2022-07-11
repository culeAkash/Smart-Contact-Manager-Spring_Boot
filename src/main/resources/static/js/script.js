
//when cross or bar is pressed if the sidebar is visible then hide it and vice versa
const toggleSidebar= ()=>{
    if($('.sidebar').is(":visible")){
        //true then close side bar
        $('.sidebar').css("display","none");
        $(".content").css("margin-left","5%");
    }
    else{
        //if not visible make sidebar visible
        $('.sidebar').css("display","block");
        $(".content").css("margin-left","26%");
    }
};

