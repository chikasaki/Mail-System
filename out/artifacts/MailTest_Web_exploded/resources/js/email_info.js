//获取emailId的url
var emailId = getQueryString("emailId");
var msgUrl = '/query/emailInfo?emailId=' + emailId;

fillMsg();

function fillMsg() {
    $.getJSON(msgUrl, function (data) {
        if(data.success){
            var email = data.email;
            $(".card-header").text(email.title);
            if(email.content.length != 0) {
                $(".email-content").text(email.content);
            }else{
                $(".email-content").html('<p class="color-gray">EMPTY</p>');
            }
            var html = '';
            email.attachments.map(function (item, index) {
                html += '<p class="color-gray">' + (index + 1) + ')' + item.fileName + '</p>';
            });
            $(".email-attachment").append(html);
        }else{
            $.toast(data.errMsg);
        }
    })
}