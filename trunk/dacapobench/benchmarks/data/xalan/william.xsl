<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

  <xsl:output encoding="utf-8"/>

<xsl:template match="/">
<html>
	<head>
		<title>
		</title>
	</head>
<body>
    <xsl:for-each select="//PLAY">
	<h1><xsl:value-of select="TITLE"/></h1>

	<table>
	<xsl:for-each select="ACT">
		<xsl:variable name="act" select="TITLE"/>
		<xsl:for-each select="SCENE">
			<xsl:value-of select="concat($act,' - ',TITLE,'   ')"/>
			Has <xsl:value-of select="count(.//SPEECH)"/> speeches
			with an average of <xsl:value-of select="round(count(.//LINE) div count(.//SPEECH))"/>
			line(s) each.
			<br/>
		</xsl:for-each>
	</xsl:for-each>
	</table>
    </xsl:for-each>
</body>
</html>
</xsl:template>

</xsl:stylesheet>
