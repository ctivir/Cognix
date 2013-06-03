$(function() {
//    $("input:submit, input:file, input:reset, button, .button").button();
               
                               
    $('.relation_link')
    .button({
        icons: {
            primary: "ui-icon-triangle-1-s"
        }
    })
    .click(function(){
        var caixa = $(this).siblings('.relation_dropdown');
        caixa.toggle()
        .addClass("dropDownBox");
        
        var btnLeft= $(this).offset().left;
        var btnTop = $(this).offset().top + $('.relation_link').outerHeight();
        var btnWidth = $(this).outerWidth();                    
        
        caixa.css({                        
            'left': btnLeft,
            'top': btnTop,                        
            'width': btnWidth                        
        })
    });
});