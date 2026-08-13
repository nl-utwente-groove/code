<?xml version="1.0" encoding="UTF-8"?>
<gxl xmlns="http://www.gupro.de/GXL/gxl-1.0.dtd">
    <graph id="graph" role="graph" edgeids="false" edgemode="directed">
        <node id="n97"/>
        <node id="n98"/>
        <node id="n99"/>
        <edge from="n99" to="n99">
            <attr name="label">
                <string>use</string>
            </attr>
        </edge>
        <edge from="n99" to="n97">
            <attr name="label">
                <string>del:a</string>
            </attr>
        </edge>
        <edge from="n98" to="n98">
            <attr name="label">
                <string>new</string>
            </attr>
        </edge>
        <edge from="n97" to="n97">
            <attr name="label">
                <string>del</string>
            </attr>
        </edge>
        <edge from="n97" to="n99">
            <attr name="label">
                <string>not:c</string>
            </attr>
        </edge>
        <edge from="n99" to="n98">
            <attr name="label">
                <string>new:b</string>
            </attr>
        </edge>
    </graph>
</gxl>
