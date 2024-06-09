console.log("this is script file")

const toggleSidebar=()=>{
    if($(".sidebar").is(":visible")){
          //band karna hai
          //true
       $(".sidebar").css("display","none");
       $(".content").css("margin-left","0%");
    }else{
       //show karna hai
       //false
        $(".sidebar").css("display","block");
        $(".content").css("margin-left","20%");
    }
}

