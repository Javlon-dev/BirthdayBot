kill -9 $(ps -ef -A | grep 'uz.bot.BotApplication' | grep java | awk '{print $2}') || echo 'process not running'
