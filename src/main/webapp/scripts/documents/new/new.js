var myform = new formtowizard({
    formid : 'newobject',                
    revealfx : [ 'slide', 500 ]
    ,
    validate: ['titulo']
});      
                                   
var numerate = function() {
    var i = 0;
                
    $("input, select, textarea").each(
        function() {
            if($(this).attr("type") === "button" || $(this).attr("type") === "file" || $(this).attr("type") === "hidden") {                
                return;
            }
            var $this = $(this);
            
            $this.addClass("ui-corner-all");

            var newName = "";
            $(this).parents(".marcador").each(
                function() {
                    if ($(this).hasClass("single")) {
                        newName = "." + $(this).attr("id")
                        + newName;
                    } else {   
                        
                        var prevNum;
                        
                        if($this.attr("type") === "checkbox" ){
                            prevNum = $this.prevAll().length;
                        } else {
                            prevNum = $(this).prevAll(
                                "#" + $(this).attr("id")).length;                             
                        }                        
                        newName = "." + $(this).attr("id") + "["
                        + prevNum + "]" + newName;
                    }
                    
                });
                
            newName = "obaa" + newName;
            $(this).attr("name", newName);
        });
		
};

$(function() {
    
    $("#browser").treeview();

                
    // keep the original "inputs" as a template:               
    var templates = new Object();
                
    $(".add, .addInParent").each(function() {
        templates[this.id] = $('li.'+this.id).filter(':first').clone().
        append("<a class='remove'/>");
        templates[this.id].find("input").val("");
        templates[this.id].find("option").attr('selected', false);
        templates[this.id].find("option :first").attr('selected', true);

        templates[this.id].find("textarea").text("");
        if(this.id == 'identifier') {
            templates[this.id] = $('li.'+this.id).filter(':first').clone().
            append("<a class='remove'/>");
            ;
            templates[this.id].find("input").val("").attr("disabled", false);
        }
    });
    
                	
    $(".add").click(
        function(e) {  
            e.preventDefault();
            var $first = $('li.'+this.id).filter(':last')  
            var $template = templates[this.id];                    
            var branches = $first
            .after($template.clone());
                    
            $("#browser").treeview({
                add : branches
                        
            });  
            refreshInputs();
        });

    //add a clone in the last brother of the 'class' passed in the 'id' button
    $(document).on("click",".addInParent",function(e) {
            e.preventDefault();
            var $template = templates[this.id];
                
            //                     var branches = $('li.'+this.id+' :last').parent().
            //                     after($template.clone());
            var branches = $(this).parent().before($template.clone());
            
            
            $("#browser").treeview({
                add : branches
            });
            refreshInputs();
        });
        
    $(document).on("click",".remove",function(event) {

        if ($(event.target).is("li")
            || $(event.target).parents("li").length) {
            
            $(event.target).parents("li").filter(":first").remove();

            refreshInputs();
        }
    });    

    // script para o upload de arquivos
    var i = 1;
    $(document).on(
        "change",
        "input.last",
        function(e) {                    
            e.preventDefault();
            $('#addfile').before(
                $('.uploadField').last().clone().children('input')
                .attr('name', 'file' + i++).val("").parent());
            $(this).removeClass("last");
            refreshInputs();                    
        });

    $("#submitButton").button({
        icons: {
            primary: "ui-icon-disk"
        }
    }).click(function() {
        $("input:text").attr("disabled", false);
        numerate();      
    })
                
    $(".Slider").each(function(idx, elm) {
                    
        var name = elm.id.replace('Slider', '');
        $('#' + elm.id).slider({
            value:0,
            min: 0,
            max: 5,
            step: 1, 
                    
            slide: function( event, ui ) {
                        
                if (ui.value < 1) {
                    $("#"+name).val("");
                }
                if (ui.value >= 1 && ui.value < 2) {
                    $("#"+name).val("Muito Baixo");
                }
                if (ui.value >= 2 && ui.value < 3 ) {
                    $("#"+name).val("Baixo");
                }
                if (ui.value >= 3 && ui.value < 4 ) {
                    $("#"+name).val("Médio");
                }
                if (ui.value >= 4 && ui.value < 5 ) {
                    $("#"+name).val("Alto");
                }
                if (ui.value >= 5) {
                    $("#"+name).val("Muito Alto");
                }
                       
            }
        });                
                
    });
    $( "#Difficulty" ).slider({
        value:0,
        min: 0,
        max: 5,
        step: 1, 
        slide: function( event, ui ) {
            if (ui.value < 1) {
                $("#amount").val("");
            }
            if (ui.value >= 1 && ui.value < 2) {
                $("#amount").val("Muito Fácil");
            }
            if (ui.value >= 2 && ui.value < 3 ) {
                $("#amount").val("Fácil");
            }
            if (ui.value >= 3 && ui.value < 4 ) {
                $("#amount").val("Médio");
            }
            if (ui.value >= 4 && ui.value < 5 ) {
                $("#amount").val("Difícil");
            }
            if (ui.value >= 5) {
                $("#amount").val("Muito Difícil");
            }
        }
    });

    $( "#slider-range" ).slider({
        range: true,
        min: 0,
        max: 100,
        values: [ 0, 0 ],
        slide: function( event, ui ) {
            $( "#age" ).val( ui.values[ 0 ] + " - " + ui.values[ 1 ] +" anos" );
            if(ui.values[0]==0 && ui.values[1]==0)
                $("#age").val("");
        }
    });

            
    $("input:submit, input:file, input:reset, button, .button").button();

//    var $dialog_old = $( "#dialog-confirm-rm-file" ).dialog({
//        resizable: true,
//        width:356,
//        autoOpen: false,
//        modal: true,
//        position: ['center',150],
//        buttons: {
//            "Apagar": function() {
//                _thisDialog = $(this);
//                jQuery.ajax($(this).data('url'), {
//                    dataType: 'text',
//                    type: 'delete',
//                    error: function(jqXHR, textStatus, errorThrown) {
//                        _thisDialog.dialog( "close" );
//                        $("#textStatus").text(textStatus);
//                        $("#errorThrown").text("Não foi possível executar a operação!");
//                        $("#dialog-error").dialog('open');
//                    },
//                    statusCode: {
//                        205: function(resultado) {
//                            var type = unescape(resultado["type"]);
//                            alert(type);
//                            _thisDialog.data('botao').closest("tr").remove();
//                            _thisDialog.dialog("close");
//                        }
//                    }
//                })
//
//            },
//            Cancel: function() {
//                $( this ).dialog( "close" );
//            }
//        }
//    });
    
    /*Dialogo de confirmacao*/
    var $dialog = $( "#dialog-confirm-rm-file" ).dialog({
        resizable: true,
        width:440,
        autoOpen: false,
        modal: true,
        buttons: [
        {
            text: "Excluir",
            id: "botaoDialogo",
            click: function() {
                _thisDialog = $(this);
                
                $.post($(this).data('url'),"",function(resultado){
                    if(resultado["type"]){
                        var type = unescape(resultado["type"]);
                        if(type =="error"){
                            _thisDialog.dialog('close');
                            $("#textStatus").text(type);
                            $("#errorThrown").text(unescape(resultado["message"]));
                            if(type == 'warn'){
                                $("#error-type").text("Warn: ");
                            }
                            $("#dialog-error").dialog('open');
                        }else{                            
                            _thisDialog.data('botao').closest("tr").remove();
                            _thisDialog.dialog("close");
                        }
                    }else{
                        _thisDialog.dialog('close');
                        $("#errorThrown").text("Não foi possível executar a operação!");
                        $("#dialog-error").dialog('open');
                    }
                },"json")
                .error(function(jqxhr) {
                    _thisDialog.dialog('close');
                    if(jqxhr.status == 403){
                        $("#errorThrown").text("Você não possui permissão para deletar esse arquivo!");
                    }else{
                        $("#errorThrown").text("Não foi possível executar a operação!");
                    }                    
                    
                    $("#dialog-error").dialog('open');
                });

                $( ".dialog-confirm" ).siblings(".ui-dialog-buttonpane").hide();
                $( ".dialog-confirm" ).html("<p> Excluindo... Por favor aguarde.</p>");
            }
        },
        {
            text: "Cancelar",
            click: function() {
                $( this ).dialog( "close" );
            }
        }
        ]
    });


    $('.delete_link').click(function(e) {
        $("#confirm-file").text($(this).attr('name'));
        e.preventDefault();
        $("#dialog-confirm-rm-file")
        .data({
            'url':  $(this).attr('href'), 
            'botao': $(this)
        }) // sends url and line to the dialog
        .dialog('open'); // opens the dialog
    });


    var parameter = location.search.substr(1).split("?");
                
    if (parameter=="classPlan"){
        $('#coverages').hide();      
        $('#cov').hide();
        $('#format').hide();
        $('#location').hide();
        $('#locationButton').hide();
        $('#installationRemarks').hide();
        $('#duration').hide();
        $('#platformSpecificFeature').hide();
        $('#platformSpecificFeatureButton').hide();
    }
    
    if($("#files").html()){
        removeInputFile();
    }
    
    $("#addFile").button( {
        icons: {
            primary: "ui-icon-circle-plus"
        }
    }).click(function(ev) {
        ev.preventDefault();
        addInputFile();
    });
    
    $("#rmFile").button( {
        icons: {
            primary: "ui-icon-circle-minus"
        }, 
        text: false
    }).click(function(ev) {
        ev.preventDefault();
        removeInputFile();
        updateHeightForm();
    });
        
    refreshInputs();
});

var removeInputFile = function(){
    var divUpload = $("#uploads");
    divUpload.find("input:file").attr('disabled','disabled');
    divUpload.hide();
    $("#addFile").show();
};

var addInputFile = function(){
    var divUpload = $("#uploads");
    divUpload.find("input:file").removeAttr('disabled');
    divUpload.show();
    $("#addFile").hide();
    $("#rmFile").show();
    updateHeightForm();
};

var refreshInputs = function() {     
    numerate();
    $("input:submit, input:file, input:reset, input:button, button, .add, .addInParent, .remove").each(function() {
        if($(this).hasClass("add") || $(this).hasClass("addInParent")) {
            //                     		$(this).text($(this).text().replace("+ ", ""));Fr
            $(this).text($(this).text().replace("+",""));

            $(this).button( {
                icons: {
                    primary: "ui-icon-circle-plus"
                }
            }).
            css({
                fontSize: '8pt'
            });
        }
        else if($(this).hasClass("remove")) {
            $(this).button( {
                icons: {
                    primary: "ui-icon-circle-minus"
                }, 
                text: false
            }).
            css({
                height: '16pt', 
                width: '16pt'
            });
        } else{
            $(this).button().css({
                fontSize: '8pt'
            });
        }
    });
    updateHeightForm();
};


var updateHeightForm = function(){    
    myform.loadsection('actual', true);
}

$(window).bind("load", function() {    
    updateHeightForm();
});