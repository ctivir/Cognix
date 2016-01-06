var myform = new formtowizard({
    formid: 'newobject',
    home: window.urlRoot,
    revealfx: ['slide', 500],
    validate: ['titulo', 'validation_locate_file']
});

var numerate = function() {
    var i = 0;

    $("input, select, textarea").each(
            function() {
                if ($(this).attr("type") === "button" || $(this).attr("type") === "file" || $(this).attr("type") === "hidden") {
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

                                if ($this.attr("type") === "checkbox") {
                                    prevNum = $this.prevAll().length;
                                } else {
                                    prevNum = $(this).prevAll(
                                            "#" + $(this).attr("id")).length;
                                }
                                newName = "." + $(this).attr("id") + "["
                                        + prevNum + "]" + newName;
                            }

                        });
                //alert(newName);
                var aux = newName.split(".");
                if ($.isArray(aux)) {
                    var aux2 = aux[2].split("[");
                    if ($.isArray(aux2)) {
                        if (aux[1] == 'arquivos' && aux2[0] == 'location') {
                            newName = newName.replace('arquivos', 'technical');
                        }
                    }
                }
                newName = "obaa" + newName;
                $(this).attr("name", newName);
            });

};

$(function() {

    $('input#location').on('keyup', function() {
        if ($(this).val() == '') {
            if ($('#validation_locate_file').val() == 'location') {
                $('#validation_locate_file').val('');
            }
        } else {
            if ($('#validation_locate_file').val() != 'file') {
                $('#validation_locate_file').val('location');
            }
        }

    });

    $('.addInParent').addClass('glyphicon glyphicon-plus-sign btn btn-default');
    $('.add').addClass('glyphicon glyphicon-plus-sign btn btn-default');

    $("#browser").treeview();


    // keep the original "inputs" as a template:               
    var templates = new Object();

    $(".add, .addInParent").each(function() {
        templates[this.id] = $('li.' + this.id).filter(':first').clone().
                prepend("<a class='remove btn btn-default btn-sm'><span class='glyphicon glyphicon-star'></span>  remover</a>");
        templates[this.id].find("input").val("");
        templates[this.id].find("option").attr('selected', false);
        templates[this.id].find("option :first").attr('selected', true);

        templates[this.id].find("textarea").text("");
        if (this.id == 'identifier') {
            templates[this.id] = $('li.' + this.id).filter(':first').clone().
                    prepend("<a class='remove btn btn-default btn-sm'><span class='glyphicon glyphicon-star'></span>  remover</a>");
            templates[this.id].find("input").val("").attr("disabled", false);
        }
    });


    $(".add").click(
            function(e) {
                e.preventDefault();
                var $first = $('li.' + this.id).filter(':last')
                var $template = templates[this.id];
                var branches = $first
                        .after($template.clone());

                $("#browser").treeview({
                    add: branches

                });
                refreshInputs();
            });

    //add a clone in the last brother of the 'class' passed in the 'id' button
    $(document).on("click", ".addInParent", function(e) {
        e.preventDefault();
        var $template = templates[this.id];

        var branches = $(this).parent().before($template.clone());

        $("#browser").treeview({
            add: branches
        });
        refreshInputs();
    });

    $(document).on("click", ".remove", function(event) {

        if ($(event.target).is("li")
                || $(event.target).parents("li").length) {

            $(event.target).parents("li").filter(":first").remove();

            refreshInputs();
        }
    });

    $("#submitButton").click(function() {
        $("input:text").attr("disabled", false);
        numerate();
    });

    $(".sliderSelect").change(function() {
        $(this).parents('li').children('div .slider').slider('value', this.selectedIndex);
    });

    $(".slider").each(function(idx, elm) {

        var name = elm.id.replace('Slider', '');
        var select = $("#" + name).find(".sliderSelect");
        //remover o texto do primeiro option do select do slider, se não fica "- Nenhum -" 
        select.find('option:eq(0)').text('');

        $('#' + elm.id).slider({
            value: select[ 0 ].selectedIndex,
            min: 0,
            max: 5,
            step: 1,
            slide: function(event, ui) {

                if (ui.value < 1) {
                    select.val("");
                }
                if (ui.value >= 1 && ui.value < 2) {
                    select.val("very low");
                }
                if (ui.value >= 2 && ui.value < 3) {
                    select.val("low");
                }
                if (ui.value >= 3 && ui.value < 4) {
                    select.val("medium");
                }
                if (ui.value >= 4 && ui.value < 5) {
                    select.val("high");
                }
                if (ui.value >= 5) {
                    select.val("very high");
                }

            }
        });
    });

    $("#slider-range").slider({
        range: true,
        min: 0,
        max: 100,
        values: [0, 0],
        slide: function(event, ui) {
            $("#age").val(ui.values[ 0 ] + " - " + ui.values[ 1 ] + " anos");
            if (ui.values[0] == 0 && ui.values[1] == 0)
                $("#age").val("");
        }
    });


    /*Dialogo de confirmacao*/
    /* var $dialog = $("#dialog-confirm-rm-file").dialog({
     resizable: true,
     width: 440,
     autoOpen: false,
     modal: true,
     buttons: [
     {
     text: "Excluir",
     id: "botaoDialogo",
     click: function() {
     _thisDialog = $(this);
     
     $.post($(this).data('url'), "", function(resultado) {
     if (resultado["type"]) {
     var type = unescape(resultado["type"]);
     if (type == "error") {
     _thisDialog.dialog('close');
     $("#textStatus").text(type);
     $("#errorThrown").text(unescape(resultado["message"]));
     if (type == 'warn') {
     $("#error-type").text("Warn: ");
     }
     $("#dialog-error").dialog('open');
     } else {
     _thisDialog.data('botao').closest("tr").remove();
     _thisDialog.dialog("close");
     }
     } else {
     _thisDialog.dialog('close');
     $("#errorThrown").text("Não foi possível executar a operação!");
     $("#dialog-error").dialog('open');
     }
     }, "json")
     .error(function(jqxhr) {
     _thisDialog.dialog('close');
     if (jqxhr.status == 403) {
     $("#errorThrown").text("Você não possui permissão para deletar esse arquivo!");
     } else {
     $("#errorThrown").text("Não foi possível executar a operação!");
     }
     
     $("#dialog-error").dialog('open');
     });
     
     $(".dialog-confirm").siblings(".ui-dialog-buttonpane").hide();
     $(".dialog-confirm").html("<p> Excluindo... Por favor aguarde.</p>");
     }
     },
     {
     text: "Cancelar",
     click: function() {
     $(this).dialog("close");
     }
     }
     ]
     });*/


//    $('.delete_link').click(function(e) {
//        $("#confirm-file").text($(this).attr('name'));
//        e.preventDefault();
//        $("#dialog-confirm-rm-file")
//                .data({
//                    'url': $(this).attr('href'),
//                    'botao': $(this)
//                }) // sends url and line to the dialog
//                .dialog('open'); // opens the dialog
//    });

    //pega o primeiro parametro depois da ?. 
    var parameter = location.search.substr(1).split("?");
    if (parameter == "classPlan") {
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

    refreshInputs();

    //atualiza o tamanho da arvore sempre que o sectionwrap tiver o tamanho alterado
    $(".sectionwrap").mutate('height', function(element, info) {
        var $this = $(this);
        clearTimeout($this.data('timer'));
        $this.data('timer', setTimeout(function() {
            updateHeightForm();
        }, 18));

    });

    $('.duration').mask('00h00min00s');

});//Jquery


var refreshInputs = function() {
    numerate();
    $("input:submit, input:file, input:reset, input:button, button, .add, .addInParent, .remove").each(function() {
        if ($(this).hasClass("add") || $(this).hasClass("addInParent")) {
            //                     		$(this).text($(this).text().replace("+ ", ""));Fr
            $(this).text($(this).text().replace("+", ""));

            $(this).css({fontSize: '8pt'});
        }
        else if ($(this).hasClass("remove")) {
            $(this).css({
                height: '16pt',
                width: '16pt'
            });
        } else {
            $(this).css({
                fontSize: '8pt'
            });
        }
    });
};


var updateHeightForm = function() {
    myform.loadsection('actual', true);
};

$(window).bind("load", function() {
    updateHeightForm();
});
