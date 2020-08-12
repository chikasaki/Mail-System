$(function() {
    var loading = false;
    // 获取店铺列表的URL
    var listUrl = '/query/searchKey';
    var listAttachUrl = '/query/searchAttachment';

    var maxItems = 999;

    var key = null;
    var isContent = 0;
    var isAttachment = 0;

    var ajax1, ajax2;

    getSearchDivData();
    // 预先加载10条店铺信息
    addItems();
    /**
     * 获取店铺类别列表以及区域列表信息
     *
     * @returns
     */
    function getSearchDivData() {
        var html = '';
        var content = '查询内容';
        var attachment = '查询附件';
        //0表示isContent,1表示isAttachment
        for(var i = 0; i < 2;i ++){
        html += '<a href="#" class="button" data-type='
            + i
            + '>'
            + (i == 0 ? content : attachment)
            + '</a>';
        }
        $('#emaillist-search-div').html(html);
    }

    /**
     * 获取分页展示的店铺列表信息
     *
     * @param pageSize
     * @param pageIndex
     * @returns
     */
    function addItems() {
        // 拼接出查询的URL，赋空值默认就去掉这个条件的限制，有值就代表按这个条件去查询
        var url = listUrl + '?' + '&key=' + key + '&isContent=' + isContent
            + '&isAttachment=' + isAttachment;
        // 设定加载符，若还在后台取数据则不能再次访问后台，避免多次重复加载
        loading = true;
        // 访问后台获取相应查询条件下的店铺列表
        ajax1 = $.getJSON(url, function(data) {
            if (data.success) {
                // 获取当前查询条件下店铺的总数
                maxItems = data.count;
                var html = '';
                // 遍历店铺列表，拼接出卡片集合
                data.emailList.map(function(item, index) {
                    html += '<div class="card" data-email-id="'
                        + item.id + '">' + '<div class="card-header">'
                        + item.title + '</div>'
                        + '<div class="card-content">'
                        + '<div class="list-block media-list">' + '<ul>'
                        + '<li class="item-content">'
                        + '<div class="item-inner">'
                        + '<div class="item-subtitle">' + item.content.substring(0, Math.min(20, item.content.length)).toString()
                        + '</div>' + '</div>' + '</li>' + '</ul>'
                        + '</div>' + '</div>' + '<div class="card-footer">'
                        + '<p class="color-gray">发送时间：'
                        + new Date(item.sendTime).Format("yyyy-MM-dd")
                        + '</p>' + '</div>'
                        + '</div>';
                });
                // 将卡片集合添加到目标HTML组件里
                $('.list-div').append(html);

                if(isAttachment == 1) {
                    //TODO:接受附件相关数据
                    ajax2 = $.getJSON(listAttachUrl, function (data2) {
                        if (data2.success) {
                            var html2 = '';

                            data2.emailList.map(function (item, index) {
                                html2 += '' + '<div class="card" data-email-id="'
                                    + item.id + '">' + '<div class="card-header">'
                                    + item.title + '</div>'
                                    + '<div class="card-content">'
                                    + '<div class="list-block media-list">' + '<ul>'
                                    + '<li class="item-content">'
                                    + '<div class="item-inner">'
                                    + '<div class="item-subtitle">' + item.content.substring(0, Math.min(20, item.content.length)).toString()
                                    + '</div>' + '</div>' + '</li>' + '</ul>'
                                    + '</div>' + '</div>' + '<div class="card-footer">'
                                    + '<p class="color-gray">发送时间：'
                                    + new Date(item.sendTime).Format("yyyy-MM-dd")
                                    + '</p>' + '</div>'
                                    + '</div>';
                            });

                            // 将卡片集合添加到目标HTML组件里
                            $('.list-div').append(html2);
                            // 刷新页面，显示新加载的店铺
                            $.refreshScroller();
                        }
                    });
                }

                $.refreshScroller();
            }
        });
    }

    // 点击店铺的卡片进入该店铺的详情页
    $('.email-list').on('click', '.card', function(e) {
        var emailId = e.currentTarget.dataset.emailId;
        window.location.href = '/route/toEmailInfo?emailId=' + emailId;
    });

    // 选择新的店铺类别之后，重置页码，清空原先的店铺列表，按照新的类别去查询
    $('#emaillist-search-div').on(
        'click',
        '.button',
        function(e) {
            var type = e.target.dataset.type;
            if(type == 0){
                isContent = 1 - isContent;
            }else{
                isAttachment = 1 - isAttachment;
            }

            if ($(e.target).hasClass('button-fill')) {
                $(e.target).removeClass('button-fill');
            } else {
                $(e.target).addClass('button-fill');
            }
        });

    // 需要查询的key发生变化后，重置页码，清空原先的email列表，按照新的key去查询
    $('#search').on('change', function(e) {
        if(ajax2 != undefined) {
            ajax2.abort();
        }
        key = e.target.value;
        $('.list-div').empty();
        pageNum = 1;
        addItems();
    });

    // 初始化页面
    $.init();
});
