<?xml version="1.0" encoding="UTF-8"?>
<gxl xmlns="http://www.gupro.de/GXL/gxl-1.0.dtd">
    <graph id="graph" role="graph" edgeids="false" edgemode="directed">
        <node id="n111"/>
        <node id="n109"/>
        <node id="n110"/>
        <node id="n112"/>
        <node id="n108"/>
        <edge from="n108" to="n108">
            <attr name="label">
                <string>Cell</string>
            </attr>
        </edge>
        <edge from="n108" to="n110">
            <attr name="label">
                <string>next</string>
            </attr>
        </edge>
        <edge from="n110" to="n109">
            <attr name="label">
                <string>next</string>
            </attr>
        </edge>
        <edge from="n112" to="n110">
            <attr name="label">
                <string>first</string>
            </attr>
        </edge>
        <edge from="n112" to="n112">
            <attr name="label">
                <string>Buffer</string>
            </attr>
        </edge>
        <edge from="n112" to="n108">
            <attr name="label">
                <string>last</string>
            </attr>
        </edge>
        <edge from="n109" to="n108">
            <attr name="label">
                <string>next</string>
            </attr>
        </edge>
        <edge from="n109" to="n109">
            <attr name="label">
                <string>Cell</string>
            </attr>
        </edge>
        <edge from="n111" to="n111">
            <attr name="label">
                <string>Object</string>
            </attr>
        </edge>
        <edge from="n110" to="n110">
            <attr name="label">
                <string>Cell</string>
            </attr>
        </edge>
        <edge from="n109" to="n111">
            <attr name="label">
                <string>val</string>
            </attr>
        </edge>
    </graph>
</gxl>
