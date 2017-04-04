const Sequelize = require('sequelize')
const sequelize = require(__dirname + '/sequelize-connection.js')

function nonNull(type) {
	return {
		type,
		allowNull: false
	}
}
const Link = sequelize.define('link', {
	college: nonNull(Sequelize.STRING(50)),
	slug: nonNull(Sequelize.STRING(32)),
	scheduledTime: Sequelize.RANGE(Sequelize.DATE), //would probably always line up with a period, but database should be flexible
	notesToCollege: Sequelize.TEXT,
	notesToCollegeSeen: Sequelize.BOOLEAN,
	notesFromCollege: Sequelize.TEXT,
	notesFromCollegeSeen: Sequelize.BOOLEAN,
	lastSignedIn: Sequelize.DATE,
	//Any more information we want from the college
	repName: Sequelize.STRING
})
const Period = sequelize.define('period', {
	day: nonNull(
		Sequelize.ENUM(
			'Monday',
			'Tuesday',
			'Wednesday',
			'Thursday',
			'Friday'
		)
	),
	period: nonNull(Sequelize.INTEGER),
	startHour: nonNull(Sequelize.INTEGER),
	startMinute: nonNull(Sequelize.INTEGER),
	minutes: nonNull(Sequelize.INTEGER)
})
const reason = Sequelize.STRING
const impossible = nonNull(Sequelize.BOOLEAN) //severity of unavailability (could it maybe be negotiated?)
const DisallowedDay = sequelize.define('disallowed_day', {
	reason,
	days: nonNull(Sequelize.RANGE(Sequelize.DATEONLY)),
	impossible
})
const DisallowedPeriod = sequelize.define('disallowed_period', {
	reason,
	timeRange: nonNull(Sequelize.RANGE(Sequelize.DATE)),
	repeatWeekly: nonNull(Sequelize.BOOLEAN),
	repeatEnd: Sequelize.DATEONLY,
	impossible
})
Link.sync()
Period.sync()
DisallowedDay.sync()
DisallowedPeriod.sync()