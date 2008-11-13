<?xml version="1.0" encoding="UTF-8"?>
<gxl xmlns="http://www.gupro.de/GXL/gxl-1.0.dtd">
    <graph id="graph" role="graph" edgeids="false" edgemode="directed">
        <node id="n112"/>
        <node id="n115"/>
        <node id="n114"/>
        <node id="n113"/>
        <node id="n111"/>
        <edge from="n112" to="n112">
            <attr name="label">
                <string>4</string>
            </attr>
        </edge>
        <edge from="n115" to="n115">
            <attr name="label">
                <string>3</string>
            </attr>
        </edge>
        <edge from="n114" to="n114">
            <attr name="label">
                <string>root</string>
            </attr>
        </edge>
        <edge from="n114" to="n111">
            <attr name="label">
                <string>list</string>
            </attr>
        </edge>
        <edge from="n113" to="n112">
            <attr name="label">
                <string>x</string>
            </attr>
        </edge>
        <edge from="n113" to="n114">
            <attr name="label">
                <string>caller</string>
            </attr>
        </edge>
        <edge from="n113" to="n113">
            <attr name="label">
                <string>control</string>
            </attr>
        </edge>
        <edge from="n113" to="n113">
            <attr name="label">
                <string>append</string>
            </attr>
        </edge>
        <edge from="n113" to="n111">
            <attr name="label">
                <string>this</string>
            </attr>
        </edge>
        <edge from="n111" to="n115">
            <attr name="label">
                <string>val</string>
            </attr>
        </edge>
    </graph>
</gxl>
